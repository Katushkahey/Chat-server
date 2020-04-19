package common;

public class Library {
    /*
    * /auth_request§login§password
    * /auth_accept§nickname
    * /auth_error
    * /msq_format_error
    * /broadcast§msg
    */

    public static final String DELIMITER = "§";
    public static final String AUTH_REQUEST = "/auth_request";
    public static final String AUTH_ACCEPT = "/auth_accept";
    public static final String AUTH_DENIED = "/auth_denied";
    // если мы не смогли разобрать, что за сообщение
    public static final String MSG_FORMAT_ERROR = "/msg_format_error";
    // сообщение, котоое будет рассылаться всем
    public static final String TYPE_BROADCAST = "/bcast";
    public static final String USER_LIST = "/user_list";

    public static String getAuthRequest (String login, String password) {
        return AUTH_REQUEST + DELIMITER + login + DELIMITER + password;
    }

    public static String getAuthAccept(String nickname) {
        return AUTH_ACCEPT + DELIMITER + nickname;
    }

    public static String getAuthDenied() {
        return AUTH_DENIED;
    }

    public static String getMsgFormatError(String message) {
        return MSG_FORMAT_ERROR + DELIMITER + message;
    }

    public static String getTypeBroadcast(String source, String message) {
        return TYPE_BROADCAST + DELIMITER + source + DELIMITER + message;
    }

    public static String getUserList(String userList) {
        return USER_LIST + DELIMITER + userList;
    }

}
