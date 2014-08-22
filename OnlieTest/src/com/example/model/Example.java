package com.example.model;

import java.io.Serializable;

import com.google.gson.annotations.Expose;

public class Example implements Serializable {
/*id	int	试题编号
 	　　subjectName	string	学科
 	　　knowledge	string	知识点
 	　　sourcename	string	试卷来源
 	　　questionTypes	string	题型
 	　　questionDifficulty	string	难易程度
 	　　question	string	试题
 	　　answer	string	答案
 	　　resolve
 * 
 * 
 * */
	@Expose
	private int id;
	@Expose
	private String subjectName;
	
	@Expose
	private String knowledge;
	
	@Expose
	private String sourcename;
	
	@Expose
	private String questionTypes;
	
	@Expose
	private String questionDifficulty;
	
	@Expose
	private String question;
	
	@Expose
	private String answer;
	
	@Expose
	private String resolve;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getSubjectName() {
		return subjectName;
	}

	public void setSubjectName(String subjectName) {
		this.subjectName = subjectName;
	}

	public String getKnowledge() {
		return knowledge;
	}

	public void setKnowledge(String knowledge) {
		this.knowledge = knowledge;
	}

	public String getSourcename() {
		return sourcename;
	}

	public void setSourcename(String sourcename) {
		this.sourcename = sourcename;
	}

	public String getQuestionTypes() {
		return questionTypes;
	}

	public void setQuestionTypes(String questionTypes) {
		this.questionTypes = questionTypes;
	}

	public String getQuestionDifficulty() {
		return questionDifficulty;
	}

	public void setQuestionDifficulty(String questionDifficulty) {
		this.questionDifficulty = questionDifficulty;
	}

	public String getQuestion() {
		return question;
	}

	public void setQuestion(String question) {
		this.question = question;
	}

	public String getAnswer() {
		return answer;
	}

	public void setAnswer(String answer) {
		this.answer = answer;
	}

	public String getResolve() {
		return resolve;
	}

	public void setResolve(String resolve) {
		this.resolve = resolve;
	}

	public Example(int id, String subjectName, String knowledge,
			String sourcename, String questionTypes, String questionDifficulty,
			String question, String answer, String resolve) {
		super();
		this.id = id;
		this.subjectName = subjectName;
		this.knowledge = knowledge;
		this.sourcename = sourcename;
		this.questionTypes = questionTypes;
		this.questionDifficulty = questionDifficulty;
		this.question = question;
		this.answer = answer;
		this.resolve = resolve;
	}
	
	
	
	
}
