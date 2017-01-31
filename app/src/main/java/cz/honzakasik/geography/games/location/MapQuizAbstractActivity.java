package cz.honzakasik.geography.games.location;

import android.content.Intent;
import android.os.Bundle;
import android.view.GestureDetector;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.mapsforge.core.model.BoundingBox;
import org.mapsforge.core.model.LatLong;
import org.mapsforge.core.model.MapPosition;
import org.mapsforge.map.android.view.MapView;
import org.mapsforge.map.layer.renderer.TileRendererLayer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

import cz.honzakasik.geography.App;
import cz.honzakasik.geography.R;
import cz.honzakasik.geography.common.location.country.Country;
import cz.honzakasik.geography.common.location.layout.TopPanelContainer;
import cz.honzakasik.geography.common.location.map.CountryPolygonOverlayHandler;
import cz.honzakasik.geography.common.location.map.MapPositionAnimator;
import cz.honzakasik.geography.common.location.map.PaintUtils;
import cz.honzakasik.geography.common.location.map.TileRendererLayerBuilder;
import cz.honzakasik.geography.common.mapquiz.MessageDisplayHandler;
import cz.honzakasik.geography.common.quiz.QuizManager;
import cz.honzakasik.geography.common.quiz.layout.QuizFooterView;
import cz.honzakasik.geography.common.quiz.question.Question;
import cz.honzakasik.geography.common.quiz.question.QuestionFactory;
import cz.honzakasik.geography.common.quiz.question.flagquestion.FlagQuestion;
import cz.honzakasik.geography.common.quiz.question.flagquestion.FlagQuestionFactory;
import cz.honzakasik.geography.common.users.DatasourceAccessException;
import cz.honzakasik.geography.common.users.User;
import cz.honzakasik.geography.common.utils.FileHelper;
import cz.honzakasik.geography.common.utils.PropUtils;
import cz.honzakasik.geography.common.utils.ResHelper;
import cz.honzakasik.geography.common.location.map.LocationGestureDetector;
import cz.honzakasik.geography.games.AbstractGameActivity;
import cz.honzakasik.geography.games.GamesConstants;
import cz.honzakasik.geography.games.ResultsActivity;
import cz.honzakasik.geography.settings.PreferenceHelper;

import static cz.honzakasik.geography.common.mapquiz.QuizAnimationConstants.LAST_MESSAGE_DISPLAY_DURATION;
import static cz.honzakasik.geography.common.mapquiz.QuizAnimationConstants.OVERLAY_ANIMATION_DURATION;

public abstract class MapQuizAbstractActivity extends AbstractGameActivity {

    private Logger logger = LoggerFactory.getLogger(MapQuizAbstractActivity.class);

    private static final byte DEFAULT_ZOOM_LEVEL = 4;
    private static final byte MAX_ZOOM_LEVEL = DEFAULT_ZOOM_LEVEL + 4;
    private static final byte MIN_ZOOM_LEVEL = DEFAULT_ZOOM_LEVEL - 1;

    protected MapView mapView;
    protected CountryPolygonOverlayHandler overlayHandler;
    protected MessageDisplayHandler messageHandler;

    protected TopPanelContainer topPanelContainer;

    private QuizFooterView quizFooterView;
    private LocationGestureDetector onTouchListener;
    protected TextView questionTextView;
    protected FlagQuestion currentQuestion;

    protected FlagQuestionFactory questionFactory = new FlagQuestionFactory.Builder()
            .context(App.getContext())
            .answersPerQuestion(1)
            .build();

    protected QuizManager<FlagQuestion> quizManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            this.quizManager = buildQuizManager(this.questionFactory);
        } catch (DatasourceAccessException e) {
            e.printStackTrace();
        }

        this.setContentView(R.layout.activity_map_quiz);

        this.questionTextView = (TextView) this.findViewById(R.id.map_quiz_container_question_text);
        this.quizFooterView = (QuizFooterView) findViewById(R.id.map_quiz_footer);
        this.topPanelContainer = (TopPanelContainer) findViewById(R.id.map_quiz_question_container);
        this.mapView = (MapView) findViewById(R.id.map_quiz_map_view);
        this.mapView.setClickable(true);
        this.mapView.getMapScaleBar().setVisible(true);
        this.mapView.setBuiltInZoomControls(true);
        this.mapView.getModel().mapViewPosition
                .setMapLimit(BoundingBox.fromString(PropUtils.get("map.boundingbox")));
        addBaseMapLayer(this.mapView);
        this.overlayHandler = new CountryPolygonOverlayHandler(this.mapView);
        overlayHandler.colorCountriesWithRandomColors(((App) getApplicationContext()).getCountries());

        this.onTouchListener = new LocationGestureDetector(this.mapView, new GuessCountryByFlagHooks());
        this.mapView.setGestureDetector(new GestureDetector(this, this.onTouchListener));

        setZoomLevels(this.mapView);
        this.mapView.getModel()
                .mapViewPosition
                .setMapPosition(
                        new MapPosition(
                                BoundingBox.fromString(PropUtils.get("map.boundingbox"))
                                        .getCenterPoint(),
                                DEFAULT_ZOOM_LEVEL));
        this.mapView.repaint();

        TextView messageBox = (TextView) findViewById(R.id.map_quiz_container_message_textview);
        this.messageHandler = new MessageDisplayHandler(messageBox, this);
        updatePointCounter();
        try {
            showUserChoiceDialogIfNeeded();
        } catch (DatasourceAccessException e) {
            e.printStackTrace();
        }
    }

    private <T extends Question> QuizManager<T> buildQuizManager(QuestionFactory<T> questionFactory)
            throws DatasourceAccessException {
        User user = null;

        if (PreferenceHelper.with(this).isDefaultUserSelected()) {

            user = userManager.getUser(PreferenceHelper.with(this).getDefaultUserId());
        }

        return new QuizManager.Builder<T>()
                .user(user)
                .difficultyLevel(getCurrentDifficultyLevelAccordingToUser())
                .gameIdentification(getGameIdentification())
                .questionCount(GamesConstants.QUESTION_COUNT_IN_QUIZ)
                .questionFactory(questionFactory)
                .build();
    }

    private void addBaseMapLayer(MapView mapView) {
        TileRendererLayer tileRendererLayer = new TileRendererLayerBuilder()
                .mapDataStore(getFile(PropUtils.get("resources.maps.ocean.path")), false, false)
                .mapDataStore(getFile(PropUtils.get("resources.maps.admin.path")), false, false)
                .themeFile(getFile(PropUtils.get("resources.renderer.theme.path")))
                .setTransparent(true)
                .model(mapView.getModel())
                .context(this)
                .tileCacheName("myCache")
                .build();
        mapView.getLayerManager().getLayers().add(tileRendererLayer);
    }

    private File getFile(String filePath) {
        return new File(FileHelper.getApplicationExternalStoragePath(this), filePath);
    }

    private void setZoomLevels(MapView mapView) {
        mapView.getModel().mapViewPosition.setZoomLevelMax(MAX_ZOOM_LEVEL);
        mapView.getModel().mapViewPosition.setZoomLevelMin(MIN_ZOOM_LEVEL);
        mapView.getMapZoomControls().setZoomLevelMin(MIN_ZOOM_LEVEL);
        mapView.getMapZoomControls().setZoomLevelMax(MAX_ZOOM_LEVEL);
    }

    private class GuessCountryByFlagHooks implements LocationGestureDetector.OnTouchHook {

        @Override
        public void afterClickedCountry(Country country) {
            logger.info("Country to guess: {}", currentQuestion.getGuessedCountry().toString());
            boolean isAnswerRight = (currentQuestion.getGuessedCountry().equals(country));
            logger.info("User answered right? {}!", isAnswerRight);
            quizManager.answeredQuestion(isAnswerRight);
            updatePointCounter();
            if (isAnswerRight) {
                messageHandler.displayMessage(String.format(getString(R.string.quiz_good_job),
                        ResHelper.getLocalizedCountryName(currentQuestion.getGuessedCountry(), App.getContext())));
                overlayHandler.highLightCountryOverlayWithoutClearing(
                        country, PaintUtils.createColor(255, 0, 255, 0), OVERLAY_ANIMATION_DURATION);
                topPanelContainer.animateAnsweredRight();
                showNextQuestionOrShowResults();
            } else {
                overlayHandler.highLightCountryOverlayWithoutClearing(
                        country, PaintUtils.createColor(255, 255, 0, 0), OVERLAY_ANIMATION_DURATION);
                topPanelContainer.animateAnsweredWrong();
                if (quizManager.hasTryLeft()) {
                    int tries = quizManager.getTriesLeft();
                    String message = String.format(
                            getResources().getQuantityString(R.plurals.quiz_tries_left, tries), tries);
                    messageHandler.displayMessage(message);
                    logger.info("Only {} tries left!", quizManager.getTriesLeft());
                } else {
                    overlayHandler.blinkAndHighLightCountryOverlayWithoutClearing(
                            currentQuestion.getGuessedCountry(), PaintUtils.createColor(255, 0, 255, 0), OVERLAY_ANIMATION_DURATION);
                    zoomAppropriatelyToArea(currentQuestion.getGuessedCountry());
                    messageHandler.displayMessage(String.format(getString(R.string.quiz_good_luck_next_time),
                            ResHelper.getLocalizedCountryName(currentQuestion.getGuessedCountry(), App.getContext())));
                    showNextQuestionOrShowResults();
                }
            }
        }
    }

    private void updatePointCounter() {
        quizFooterView.updatePenalizationCount(quizManager.getTriesLeft());
        quizFooterView.updatePointCount(quizManager.getTotalPointCount());
    }

    private void showNextQuestionOrShowResults() {
        this.onTouchListener.setIsTouchDisabled(true);
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //runs on UI thread after specified time
                        if (quizManager.hasNext()) {
                            overlayHandler.clearAllStackedHighlights();
                            animateToMapPosition(mapView, new MapPosition(
                                    BoundingBox.fromString(PropUtils.get("map.boundingbox"))
                                        .getCenterPoint(),
                                    DEFAULT_ZOOM_LEVEL));
                            fillInQuestion(quizManager.next());
                            logger.info("Moved to next question!");
                            onTouchListener.setIsTouchDisabled(false);
                        } else {
                            logger.info("Showing results!");
                            startResultActivity();
                        }
                    }
                });
            }
        }, LAST_MESSAGE_DISPLAY_DURATION);

    }

    private void zoomAppropriatelyToArea(Country country) {
        int area = country.getArea();
        byte zoom = DEFAULT_ZOOM_LEVEL;
        LatLong countryCenter = country.getCenter();
        if (area > 150 && area < 7000) {//TODO
            zoom += 2;
        } else if (area <= 150) {
            zoom += 4;
        }
        animateToMapPosition(this.mapView, new MapPosition(countryCenter, zoom));
    }

    private void animateToMapPosition(MapView mapView, MapPosition mapPosition) {
        new MapPositionAnimator(mapView).animateToPosition(mapPosition, 500);
    }

    protected abstract void fillInQuestion(FlagQuestion question);

    private void startResultActivity() {
        this.finish();
        EventBus.getDefault().postSticky(quizManager);
        Intent intent = new Intent(this, ResultsActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.mapView.destroyAll();
    }
}
