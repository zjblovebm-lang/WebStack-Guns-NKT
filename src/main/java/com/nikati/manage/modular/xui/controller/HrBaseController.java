package com.nikati.manage.modular.xui.controller;


import com.nikati.manage.modular.xui.model.ResultModel;

/**
 * @author qishi
 * 2019年11月21日
 */
public class HrBaseController {

    /**
     * 返回失败状态
     *
     * @return result
     */
    protected ResultModel resultError() {
        ResultModel result = new ResultModel();
        result.setStatus(false);
        result.setMsg("操作失败！");
        return result;
    }

    /**
     * 返回失败状态并携带消息
     *
     * @return result
     */
    protected ResultModel resultErrorAndMsg(String msg) {
        ResultModel result = new ResultModel();
        result.setStatus(false);
        result.setMsg(msg);
        return result;
    }

    /**
     * 返回成功状态
     *
     * @return result
     */
    protected ResultModel resultSuccess() {
        ResultModel result = new ResultModel();
        result.setStatus(true);
        result.setMsg("操作成功！");
        return result;
    }

    /**
     * 返回成功状态
     *
     * @return result
     */
    protected ResultModel resultSuccessAndMsg(String msg) {
        ResultModel result = new ResultModel();
        result.setStatus(true);
        result.setMsg(msg);
        return result;
    }

    /**
     * 返回成功状态并携带数据
     *
     * @return result
     */
    protected ResultModel resultSuccessAndData(Object data) {
        ResultModel result = new ResultModel();
        result.setStatus(true);
        result.setData(data);
        result.setMsg("操作成功！");
        return result;
    }

    /**
     * 返回状态并携带消息
     *
     * @return result
     */
    protected ResultModel resultStatusAndMag(boolean status, String msg) {
        ResultModel result = new ResultModel();
        result.setStatus(status);
        result.setMsg(msg);
        return result;
    }

    /**
     * 返回状态并携带消息
     *
     * @return result
     */
    protected ResultModel resultStatus(boolean status) {
        ResultModel result = new ResultModel();
        result.setStatus(status);
        if (status) {
            result.setMsg("操作成功！");
        } else {
            result.setMsg("操作失败！");
        }

        return result;
    }

    /**
     * 返回状态并携带消息
     *
     * @return result
     */
    protected ResultModel resultStatusAndStatusNumAndMag(boolean status, String statusNum, String msg) {
        ResultModel result = new ResultModel();
        result.setStatus(status);
        result.setStatusNum(statusNum);
        result.setMsg(msg);
        return result;
    }

    /**
     * 返回未登录状态并携带消息
     *
     * @return result
     */
    protected ResultModel resultNoLogin() {
        ResultModel result = new ResultModel();
        result.setStatus(false);
        result.setStatusNum("302");
        result.setMsg("noLogin");
        return result;
    }


}

