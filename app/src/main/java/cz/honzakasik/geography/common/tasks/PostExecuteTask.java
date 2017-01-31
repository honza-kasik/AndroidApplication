package cz.honzakasik.geography.common.tasks;

public interface PostExecuteTask<T extends Object> {

    void run(T result);
}
