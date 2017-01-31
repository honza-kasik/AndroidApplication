package cz.honzakasik.geography.common.results;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import cz.honzakasik.geography.common.quiz.question.DifficultyLevel;
import cz.honzakasik.geography.common.users.User;

@DatabaseTable(tableName = "results")
public class Result {

    @DatabaseField(generatedId = true, canBeNull = false)
    private Integer id;

    @DatabaseField(foreign = true, foreignAutoCreate = true, foreignAutoRefresh = true, canBeNull = false)
    private User user;

    @DatabaseField(canBeNull = false)
    private Integer score;

    @DatabaseField(canBeNull = false)
    private GameIdentification gameId;

    /**
     * Need to specify one to compare only results within same difficulty level
     */
    @DatabaseField(canBeNull = false)
    private DifficultyLevel difficultyLevel;

    //ORM lite constructor
    private Result() {

    }

    public Result(User user, Integer score, GameIdentification gameId, DifficultyLevel difficultyLevel) {
        this.user = user;
        this.score = score;
        this.gameId = gameId;
        this.difficultyLevel = difficultyLevel;
    }

    public User getUser() {
        return user;
    }

    public GameIdentification getGameId() {
        return gameId;
    }

    public Integer getScore() {
        return score;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public void setGameId(GameIdentification gameId) {
        this.gameId = gameId;
    }

    public void setDifficultyLevel(DifficultyLevel difficultyLevel) {
        this.difficultyLevel = difficultyLevel;
    }

    @Override
    public String toString() {
        return "Result{" +
                "id=" + id +
                ", user=" + user +
                ", score=" + score +
                ", gameId=" + gameId +
                ", difficultyLevel=" + difficultyLevel +
                '}';
    }
}
