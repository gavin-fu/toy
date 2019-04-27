package ren.gavin.export.util.parser;

public class GenericTokenParser {

    private String openToken;

    private String closeToken;

    private TokenHandler tokenHandler;

    public GenericTokenParser(String openToken, String closeToken, TokenHandler tokenHandler) {
        this.openToken = openToken;
        this.closeToken = closeToken;
        this.tokenHandler = tokenHandler;
    }

    public String parser(String content) {
        if (null == content || content.length() == 0) {
            return "";
        }

        StringBuilder builder = new StringBuilder();
        StringBuilder expression = null;
        char[] chars = content.toCharArray();
        int start, end, offset = 0;

        start = content.indexOf(openToken);
        while (start != -1) {
            builder.append(chars, offset, start - offset);
            offset = start + openToken.length();

            end = content.indexOf(closeToken, offset);
            if (end != -1) {
                if (null == expression) {
                    expression = new StringBuilder();
                } else {
                    expression.setLength(0);
                }
                expression.append(chars, offset, end - offset);
                builder.append(tokenHandler.handleToken(expression.toString()));
                offset = end + closeToken.length();
            } else {
                builder.append(openToken);
            }
            start = content.indexOf(openToken, offset);
        }
        return builder.append(chars, offset, content.length() - offset).toString();
    }
}
