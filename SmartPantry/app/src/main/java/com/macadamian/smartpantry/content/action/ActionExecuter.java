package com.macadamian.smartpantry.content.action;

public class ActionExecuter {

    private static ActionExecuter instance = null;
    private static ActionInterface mLastAction;

    protected ActionExecuter() {
    }

    public static ActionExecuter getInstance() {
        if (instance == null) {
            instance = new ActionExecuter();
        }
        return instance;
    }

    public void execute(ActionInterface executor) {
        if (executor != null) {
            if (mLastAction != null) {
                mLastAction.execute();
            }
            mLastAction = executor;
        }
    }

    public void executeImmediately(ActionInterface executor){
        executor.execute();
    }

    public void execute() {
        if (mLastAction != null) {
            mLastAction.execute();
            mLastAction = null;
        }
    }

    public void undo() {
        if (mLastAction != null) {
            mLastAction.revert();
            mLastAction = null;
        }
    }

    public void clearLastAction(){
        mLastAction = null;
    }
}
