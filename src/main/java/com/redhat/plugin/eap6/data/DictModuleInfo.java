package com.redhat.plugin.eap6.data;

public class DictModuleInfo {

	private String name;
	private boolean needsPomSlot;
	private String slot;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isNeedsPomSlot() {
		return needsPomSlot;
	}

	public void setNeedsPomSlot(boolean needsPomSlot) {
		this.needsPomSlot = needsPomSlot;
	}

	public String getSlot() {
		return slot;
	}

	public void setSlot(String slot) {
		this.slot = slot;
	}
}
