package rokclock;

import java.awt.Color;
import java.io.*;
import java.text.*;
import java.util.Properties;

class Config {
	static final String dfS = "dd/MM/yyyy HH:mm:ss";
	static final DateFormat df = new SimpleDateFormat(dfS);
	private final String configFilename = "config.txt";
	private final String configFilenameDefault = configFilename + ".default";
	private final Properties properties = new Properties();
	private final String USER_HOME;
	
	enum AutoCountTowards {PREVIOUS, UNKNOWN, NOTHING}
	enum Behaviour {MINIMISE, HIDE, SHOW}

	Config() throws IOException {
		File configFile = new File(configFilename);
		if (!configFile.exists())
			Main.copyFile(new File(configFilenameDefault), configFile);
		properties.load(new FileInputStream(configFile));
		USER_HOME = System.getProperty("user.home");
	}

	String getProjectsFilename() {
		return processFilePath(get("projectsFilename", "projects.txt"));
	}
	
	String getProjectsFilenameDefault() {
		return "projects.txt.default";
	}

	String getLogFilename() {
		return processFilePath(get("logFilename", "log.txt"));
	}

	int getIntervalInSeconds() {
		return get("intervalInSeconds", 3600);
	}
	
	int getWaitInSeconds() {
		return get("waitInSeconds", 3600);
	}

	AutoCountTowards getAutoCountTowards() {
		return get(AutoCountTowards.class, AutoCountTowards.PREVIOUS);
	}

	Behaviour getBehaviour() {
		return get(Behaviour.class, Behaviour.MINIMISE);
	}

	boolean getWriteTimeouts() {
		return get("writeTimeouts", false);
	}

	String getTitle() {
		return "RokClock";
	}

	int getLocX() {
		return get("locX", 400);
	}

	int getLocY() {
		return get("locY", 400);
	}

	int getWidth() {
		return get("width", 150);
	}

	int getHeight() {
		return get("height", 400);
	}

	Color getDefaultColor() {
		return get("defaultColor", Color.GREEN);
	}

	Color getActiveColor() {
		return get("activeColor", Color.RED);
	}

	Color getSemiActiveColor() {
		return get("semiActiveColor", Color.CYAN);
	}

	@SuppressWarnings("unchecked")
	private <T> T get(String key, T defaultValue) {
		String v = properties.getProperty(key);
		try {
			if (v == null)
				return defaultValue;
			if (defaultValue instanceof String)
				return (T) v;
			if (defaultValue instanceof Boolean)
				return (T) Boolean.valueOf(v);
			if (defaultValue instanceof Integer)
				return (T) Integer.valueOf(v);
			if (defaultValue instanceof Color) {
				String[] rgb = v.split(",");
				return (T) new Color(Integer.parseInt(rgb[0]), Integer
						.parseInt(rgb[1]), Integer.parseInt(rgb[2]));
			}
		} catch (Exception e) {
			System.err.println("Couldn't parse "
					+ defaultValue.getClass().getSimpleName().toLowerCase()
					+ " specification for '" + key + "': " + v);
		}
		return defaultValue;
	}

	private <T extends Enum<T>> T get(Class<T> c, T defaultValue) {
		String propertyName = c.getSimpleName();
		propertyName = propertyName.substring(0, 1).toLowerCase() + propertyName.substring(1);
		String defaultValueS = defaultValue.toString().toLowerCase();
		String value = get(propertyName, defaultValueS).toUpperCase();
		T result = defaultValue;
		try {result = Enum.valueOf(c, value);}
		catch (IllegalArgumentException e) {
			System.err.println("Could not recognise the specified option for '"
					+ propertyName + "': "
					+ properties.getProperty("behaviour"));
		}
		return result;
	}

	private String processFilePath(String path) {
		return path.replace("~", USER_HOME);
	}
}