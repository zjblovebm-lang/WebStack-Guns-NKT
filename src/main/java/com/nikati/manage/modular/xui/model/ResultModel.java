package com.nikati.manage.modular.xui.model;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.io.Serializable;

/**
 * @author qishi
 * 2019年11月21日
 */
@Data
@ApiModel(description="标准返回数据结构")
public class ResultModel<T> implements Serializable {
	private static final long serialVersionUID = -1;

	private String 	msg;
	private String 	statusNum;
	private boolean status;
	private T 	data;

	public static <T> ResultModel success(T data){
		return success(null, data);
	}

	public static <T> ResultModel success(String msg, T data){
		ResultModel model = new ResultModel();
		model.setMsg(msg);
		model.setData(data);
		model.setStatus(true);
		return model;
	}

	public static <T> ResultModel error(String msg){
		ResultModel model = new ResultModel();
		model.setMsg(msg);
		return model;
	}
}