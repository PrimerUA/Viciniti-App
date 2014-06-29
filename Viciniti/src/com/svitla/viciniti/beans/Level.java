package com.svitla.viciniti.beans;

public class Level {

	private String name;
	private Integer level;
	
	public Level(String name, Integer level) {
		super();
		this.name = name;
		this.level = level;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getLevel() {
		return level;
	}

	public void setLevel(Integer level) {
		this.level = level;
	}

}
