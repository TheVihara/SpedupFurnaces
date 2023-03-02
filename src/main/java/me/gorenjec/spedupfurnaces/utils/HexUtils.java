package me.gorenjec.spedupfurnaces.utils;

import net.md_5.bungee.api.ChatColor;

import java.awt.*;
import java.util.List;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public final class HexUtils {

    private static final Pattern RAINBOW_PATTERN = Pattern.compile("<(rainbow|r)(:\\d*\\.?\\d+){0,2}>");
    private static final Pattern GRADIENT_PATTERN = Pattern.compile("<(gradient|g)(:#([A-Fa-f0-9]){6})*>");
    private static final List<Pattern> HEX_PATTERNS = Arrays.asList(Pattern.compile("<#([A-Fa-f0-9]){6}>"), Pattern.compile("&#([A-Fa-f0-9]){6}"), Pattern.compile("#([A-Fa-f0-9]){6}"));
    private static final Pattern STOP = Pattern.compile("<(gradient|g)(:#([A-Fa-f0-9]){6})*>|<(rainbow|r)(:\\d*\\.?\\d+){0,2}>|(&[a-f0-9r])|<#([A-Fa-f0-9]){6}>|&#([A-Fa-f0-9]){6}|#([A-Fa-f0-9]){6}|§");

    private HexUtils() {
    }

    public static String colorify(String message) {
        String parsed = parseRainbow(message);
        parsed = parseGradients(parsed);
        parsed = parseHex(parsed);
        parsed = parseLegacy(parsed);
        return parsed;
    }

    public static String colorify(String message, boolean useGradient) {
        String parsed = parseRainbow(message);
        if (useGradient) {
            parsed = parseGradients(parsed);
        }

        parsed = parseHex(parsed);
        parsed = parseLegacy(parsed);
        return parsed;
    }

    public static String randomHex() {
        Random obj = new Random();
        int randd_num = obj.nextInt(0xffffff + 1);

        String hexCode = String.format("#%06x", randd_num);

        return hexCode;
    }

    public static List<String> listOfRainbowFrames(String text, int numberOfColors, float saturation, float brightness) {
        List<String> lines = new ArrayList();
        Rainbow rainbow = new Rainbow(numberOfColors, saturation, brightness);

        for(int i = 0; i < numberOfColors; ++i) {
            String var10001 = translateHex(rainbow.next());
            lines.add(var10001 + text);
        }

        return lines;
    }

    public static String rainbowShifted(String text, int shiftFor, float saturation, float brightness) {
        Rainbow rainbow = new Rainbow(text.length(), saturation, brightness);
        StringBuilder parsedRainbow = new StringBuilder();
        rainbow.shift(shiftFor);
        char[] var6 = text.toCharArray();
        int var7 = var6.length;

        for(int var8 = 0; var8 < var7; ++var8) {
            char c = var6[var8];
            parsedRainbow.append(translateHex(rainbow.next())).append(c);
        }

        return parsedRainbow.toString();
    }

    private static String parseRainbow(String message) {
        String parsed = message;

        for(Matcher matcher = RAINBOW_PATTERN.matcher(message); matcher.find(); matcher = RAINBOW_PATTERN.matcher(parsed)) {
            StringBuilder parsedRainbow = new StringBuilder();
            String match = matcher.group();
            int tagLength = match.startsWith("<ra") ? 8 : 2;
            int indexOfClose = match.indexOf(">");
            String extraDataContent = match.substring(tagLength, indexOfClose);
            double[] extraData;
            if (!extraDataContent.isEmpty()) {
                extraDataContent = extraDataContent.substring(1);
                extraData = Arrays.stream(extraDataContent.split(":")).mapToDouble(Double::parseDouble).toArray();
            } else {
                extraData = new double[0];
            }

            float saturation = extraData.length > 0 ? (float)extraData[0] : 1.0F;
            float brightness = extraData.length > 1 ? (float)extraData[1] : 1.0F;
            int stop = findStop(parsed, matcher.end());
            String content = parsed.substring(matcher.end(), stop);
            Rainbow rainbow = new Rainbow(content.length(), saturation, brightness);
            char[] var14 = content.toCharArray();
            int var15 = var14.length;

            for(int var16 = 0; var16 < var15; ++var16) {
                char c = var14[var16];
                parsedRainbow.append(translateHex(rainbow.next())).append(c);
            }

            String before = parsed.substring(0, matcher.start());
            String after = parsed.substring(stop);
            parsed = before + parsedRainbow + after;
        }

        return parsed;
    }

    private static String parseGradients(String message) {
        String parsed = message;

        for(Matcher matcher = GRADIENT_PATTERN.matcher(message); matcher.find(); matcher = GRADIENT_PATTERN.matcher(parsed)) {
            StringBuilder parsedGradient = new StringBuilder();
            String match = matcher.group();
            int tagLength = match.startsWith("<gr") ? 10 : 3;
            int indexOfClose = match.indexOf(">");
            String hexContent = match.substring(tagLength, indexOfClose);
            List<Color> hexSteps = (List)Arrays.stream(hexContent.split(":")).map(Color::decode).collect(Collectors.toList());
            int stop = findStop(parsed, matcher.end());
            String content = parsed.substring(matcher.end(), stop);
            Gradient gradient = new Gradient(hexSteps, content.length());
            char[] var12 = content.toCharArray();
            int var13 = var12.length;

            for(int var14 = 0; var14 < var13; ++var14) {
                char c = var12[var14];
                parsedGradient.append(translateHex(gradient.next())).append(c);
            }

            String before = parsed.substring(0, matcher.start());
            String after = parsed.substring(stop);
            parsed = before + parsedGradient + after;
        }

        return parsed;
    }

    private static String parseHex(String message) {
        String parsed = message;
        Iterator var2 = HEX_PATTERNS.iterator();

        while(var2.hasNext()) {
            Pattern pattern = (Pattern)var2.next();

            for(Matcher matcher = pattern.matcher(parsed); matcher.find(); matcher = pattern.matcher(parsed)) {
                String color = translateHex(cleanHex(matcher.group()));
                String before = parsed.substring(0, matcher.start());
                String after = parsed.substring(matcher.end());
                parsed = before + color + after;
            }
        }

        return parsed;
    }

    private static String parseLegacy(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    private static int findStop(String content, int searchAfter) {
        Matcher matcher = STOP.matcher(content);

        do {
            if (!matcher.find()) {
                return content.length() - 1;
            }
        } while(matcher.start() <= searchAfter);

        return matcher.start();
    }

    private static String cleanHex(String hex) {
        if (hex.startsWith("<")) {
            return hex.substring(1, hex.length() - 1);
        } else {
            return hex.startsWith("&") ? hex.substring(1) : hex;
        }
    }

    public static String translateHex(String hex) {
        return ChatColor.of(hex).toString();
    }

    public static String translateHex(Color color) {
        return ChatColor.of(color).toString();
    }

    public static class Rainbow {
        private final float hueStep;
        private final float saturation;
        private final float brightness;
        private float hue;

        public Rainbow(int totalColors, float saturation, float brightness) {
            if (totalColors < 1) {
                throw new IllegalArgumentException("Must have at least 1 total color");
            } else if (!(0.0F > saturation) && !(saturation > 1.0F)) {
                if (!(0.0F > brightness) && !(brightness > 1.0F)) {
                    this.hueStep = 1.0F / (float)totalColors;
                    this.saturation = saturation;
                    this.brightness = brightness;
                    this.hue = 0.0F;
                } else {
                    throw new IllegalArgumentException("Saturation must be between 0.0 and 1.0");
                }
            } else {
                throw new IllegalArgumentException("Saturation must be between 0.0 and 1.0");
            }
        }

        public Rainbow(int totalColors) {
            this(totalColors, 1.0F, 1.0F);
        }

        public Color next() {
            Color color = Color.getHSBColor(this.hue, this.saturation, this.brightness);
            this.hue += this.hueStep;
            return color;
        }

        public void shift(int shift) {
            this.hue += this.hueStep * (float)shift;
        }
    }

    public static class Gradient {
        private final List<Color> colors;
        private final int stepSize;
        private int step;
        private int stepIndex;

        public Gradient(List<Color> colors, int totalColors) {
            if (colors.size() < 2) {
                throw new IllegalArgumentException("Must provide at least 2 colors");
            } else if (totalColors < 1) {
                throw new IllegalArgumentException("Must have at least 1 total color");
            } else {
                this.colors = colors;
                this.stepSize = totalColors / (colors.size() - 1);
                this.step = this.stepIndex = 0;
            }
        }

        public Color next() {
            return (Color)this.colors.get(0);
        }

        public static Color getGradientInterval(Color start, Color end, float interval) {
            if (!(0.0F > interval) && !(interval > 1.0F)) {
                int r = (int)((float)end.getRed() * interval + (float)start.getRed() * (1.0F - interval));
                int g = (int)((float)end.getGreen() * interval + (float)start.getGreen() * (1.0F - interval));
                int b = (int)((float)end.getBlue() * interval + (float)start.getBlue() * (1.0F - interval));
                return new Color(r, g, b);
            } else {
                throw new IllegalArgumentException("Interval must be between 0 and 1 inclusively.");
            }
        }
    }
}

