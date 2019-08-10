package org.jim.bukkit.audit.menu;


public class MenuItem {

	public static final OnClickListener back = context -> {
		context.back();
		return true;
	};

	public static final OnClickListener enter = context -> {
		context.click();
		return true;
	};

	boolean enabled = true;

	String label;

	MenuFolder parent;

	OnClickListener onClickListener;

	public MenuItem(String label) {
		super();
		this.label = label;
	}



	public MenuItem(String label, OnClickListener onClickListener) {
		super();
		this.label = label;
		this.onClickListener = onClickListener;
	}


	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public String getLabel() {
		return label;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((label == null) ? 0 : label.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MenuItem other = (MenuItem) obj;
		if (label == null) {
			if (other.label != null)
				return false;
		} else if (!label.equals(other.label))
			return false;
		return true;
	}

	public boolean isFolder() {
		return this instanceof MenuFolder;
	}

	public void setOnClickListener(OnClickListener onClickListener) {
		this.onClickListener = onClickListener;
	}

}
