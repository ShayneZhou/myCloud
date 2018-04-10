package com.example.ramsey.myCloud;

/**
 * Created by hello on 2018/4/3.
 */

public class sCause {
    private String cause_uid;
    private String cause;
    private String analysis;
    private String prob_uid;

    public String getCause_uid() {
        return cause_uid;
    }

    public String getCause()
    {
        return cause;
    }

    public String getAnalysis()
    {
        return analysis;
    }

    public String getProb_uid()
    {
        return prob_uid;
    }

    public sCause(String mcause_uid, String mcause, String manalysis, String mprob_uid)
    {
        cause_uid=mcause_uid;
        cause=mcause;
        analysis=manalysis;
        prob_uid=mprob_uid;
    }

}
