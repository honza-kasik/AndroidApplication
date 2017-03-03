package cz.honzakasik.geography.games;

import android.animation.ObjectAnimator;
import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.PreparedQuery;

import org.greenrobot.eventbus.EventBus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.List;

import cz.honzakasik.geography.R;
import cz.honzakasik.geography.common.datasource.OrmLiteBaseAppCompatActivity;
import cz.honzakasik.geography.common.quiz.QuizManager;
import cz.honzakasik.geography.common.results.Result;
import cz.honzakasik.geography.common.users.DatasourceAccessException;
import cz.honzakasik.geography.common.users.ORMLiteUserManager;
import cz.honzakasik.geography.common.users.User;
import cz.honzakasik.geography.settings.PreferenceHelper;

public class ResultsActivity extends OrmLiteBaseAppCompatActivity {

    private Logger logger = LoggerFactory.getLogger(ResultsActivity.class);

    private QuizManager quizManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.setContentView(R.layout.activity_result);
        quizManager = EventBus.getDefault().removeStickyEvent(QuizManager.class);

        RatingBar myRatingBar = (RatingBar) findViewById(R.id.results_rating_bar);

        logger.info("Max points is {}", quizManager.getMaxTotalPointCount());

        final int totalPointCount = quizManager.getTotalPointCount();
        final int maxPointCount = quizManager.getMaxTotalPointCount();
        final float ratio = (totalPointCount / (float)maxPointCount);
        final float ratingBarValue = ratio * myRatingBar.getNumStars();
        final int percentValue = (int) (ratio * 100);

        ObjectAnimator anim = ObjectAnimator.ofFloat(myRatingBar, "rating", 0f, ratingBarValue);
        anim.setDuration(3000);
        anim.start();

        TextView percentTextView = (TextView) findViewById(R.id.results_percent);
        animateText(percentTextView, 0, percentValue, new AnimatedValuePlaceholder() {
            @Override
            public String getString(Object value) {
                return String.format(getApplicationContext().getResources()
                        .getString(R.string.results_percent), (int)value);
            }
        });

        TextView totalPointCountView = (TextView) findViewById(R.id.results_total_point_count);
        animateText(totalPointCountView, 0, quizManager.getTotalPointCount(), new AnimatedValuePlaceholder() {
            @Override
            public String getString(Object value) {
                return String.format(getApplicationContext().getResources()
                                .getQuantityString(R.plurals.results_points_got_message,
                                        totalPointCount),
                        value,
                        maxPointCount);
            }
        });

        //if multiple users enabled follows
        try {
            if (PreferenceHelper.with(this).isUserManagementEnabled() &&
                PreferenceHelper.with(this).isDefaultUserSelected()) {
                saveResult();
                showTopFiveResults();
            }
        } catch (SQLException | DatasourceAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onSupportNavigateUp() {
        this.finish();
        return true;
    }

    private PreparedQuery<Result> prepareQueryForResultSaving(Dao<Result, ?> resultsDao)
            throws SQLException {
        return resultsDao.queryBuilder()
                .where()
                .eq("gameId", quizManager.getGameIdentification())
                .and()
                .eq("difficultyLevel", quizManager.getDifficultyLevel())
                .and()
                .eq("user_id", PreferenceHelper.with(this).getDefaultUserId())
                .prepare();
    }

    private void saveResult() throws DatasourceAccessException, SQLException {
        Dao<Result, ?> resultsDao = getHelper().getDao(Result.class);

        List<Result> result = resultsDao.query(prepareQueryForResultSaving(resultsDao));

        if (result.size() == 1) {
            Result updateResult = result.get(0);
            if (quizManager.getTotalPointCount() > updateResult.getScore()) {
                updateResult.setScore(quizManager.getTotalPointCount());
            }
            resultsDao.update(updateResult);

        } else if (result.size() == 0) {
            Integer defaultUserId = PreferenceHelper.with(this).getDefaultUserId();
            User defaultUser = new ORMLiteUserManager(getHelper().getDao(User.class))
                    .getUser(defaultUserId);

            resultsDao.create(new Result(
                    defaultUser,
                    quizManager.getTotalPointCount(),
                    quizManager.getGameIdentification(),
                    quizManager.getDifficultyLevel()));
        } else {
            throw new IllegalStateException("Multiple users with same id??");
        }
    }

    private void animateText(final TextView textView, Integer startValue, Integer endValue,
                            final AnimatedValuePlaceholder placeholder) {
        ValueAnimator animator = new ValueAnimator();
        animator.setObjectValues(startValue, endValue);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator animation) {
                textView.setText(placeholder.getString(animation.getAnimatedValue()));
            }
        });
        animator.setEvaluator(new TypeEvaluator<Integer>() {
            public Integer evaluate(float fraction, Integer startValue, Integer endValue) {
                return Math.round(startValue + (endValue - startValue) * fraction);
            }
        });
        animator.setDuration(3000);
        animator.start();
    }

    private interface AnimatedValuePlaceholder {
        String getString(Object value);
    }

    private PreparedQuery<Result> getPreparedQueryForTopFiveResults() throws SQLException {
        return getHelper().getDao(Result.class)
                .queryBuilder()
                .orderBy("score", false)
                .limit((long)5)
                .groupBy("user_id")
                .where()
                .eq("gameId", quizManager.getGameIdentification())
                .and()
                .eq("difficultyLevel", quizManager.getDifficultyLevel())
                .prepare();
    }

    private void showTopFiveResults() throws SQLException {
        logger.info("Showing result!");
        LinearLayout resultTable = (LinearLayout) findViewById(R.id.top_5_results);

        List<Result> topResults = getHelper().getDao(Result.class)
                .query(getPreparedQueryForTopFiveResults());

        int n = 1; //table row index displayed in ui
        for (int i = 0; i < 5; i++) {//Result result : topResults) {
            Result result = null;
            if (i < topResults.size()) {
                result = topResults.get(i);
            }

            resultTable.addView(
                populateInflatedView(inflateTopResultsListItem(), result, n++));
        }
    }

    private View inflateTopResultsListItem() {
        logger.info("Inflating result row!");
        final LayoutInflater inflater = (LayoutInflater)this
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        return inflater.inflate(R.layout.list_item_result, null);
    }

    private View populateInflatedView(View view, Result result, int n) {
        TextView order = (TextView) view.findViewById(R.id.result_item_number);
        TextView name = (TextView) view.findViewById(R.id.result_item_name);
        TextView score = (TextView) view.   findViewById(R.id.result_item_score);

        order.setText(String.valueOf(n) + ".");
        if (result != null) {
            logger.info("Populating result row with result: {}", result.toString());
            name.setText(result.getUser().getNickName());
            score.setText(String.valueOf(result.getScore()));
        } else {
            name.setText("");
            score.setText("");
        }

        return view;
    }
}
