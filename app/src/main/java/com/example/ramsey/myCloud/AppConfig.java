package com.example.ramsey.myCloud;

/**
 * Created by young on 2018/3/16.
 */

public class AppConfig {
    // Server user login url
    public static String URL_LOGIN = "http://10.0.2.2/myCloud/Login.php";

    // Server user register url
    public static String URL_REGISTER = "http://10.0.2.2/myCloud/Register.php";

    // Server user resetPwd url
    public static String URL_RESET = "http://10.0.2.2/myCloud/ResetPwd.php";

    // File upload url (replace the ip with your server address)
    public static final String FILE_UPLOAD_URL = "http://10.0.2.2/myCloud/FileUpload.php";

    // Directory name to store captured images and videos
    public static final String IMAGE_DIRECTORY_NAME = "Android File Upload";

    // Server create url
    public static final String URL_CREATE = "http://10.0.2.2/myCloud/CreateProblemList.php";
}

//10.0.2.2