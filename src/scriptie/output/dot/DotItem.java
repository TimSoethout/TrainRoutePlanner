package scriptie.output.dot;

import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

public abstract class DotItem {

	public DotItem(String label) {
		this.attributes = new TreeMap<String, String>();
		setLabel(label);
	}

	public Map<String, String> attributes;

	public static String labelKey = "label";

	public void setLabel(String label) {
		attributes.put(labelKey, label);
	}

	public String getLabel() {
		return attributes.get(labelKey);
	}

	public abstract String toString();

	protected String getAttributesString() {
		StringBuilder retVal = new StringBuilder(attributes.size() * 10);
		for (Entry<String, String> pair : attributes.entrySet()) {
			retVal.append(String.format("\"%s\"=\"%s\",", pair.getKey(),
					pair.getValue()));
		}
		retVal.deleteCharAt(retVal.length() - 1); // Remove last ','
		return retVal.toString();
	}
}
