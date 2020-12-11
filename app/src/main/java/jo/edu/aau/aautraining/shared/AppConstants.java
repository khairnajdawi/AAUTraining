package jo.edu.aau.aautraining.shared;

public class AppConstants {
    public static final String API_BASE_URL = "https://aautraining.000webhostapp.com/";
    //Login
    public static final String API_LOGIN_URL = API_BASE_URL + "login.php";
    public static final String API_TOKEN_LOGIN_URL = API_BASE_URL + "login_with_token.php";
    public static final String API_GET_STUDENT_INFO = API_BASE_URL + "get_student_info.php";
    public static final String API_GET_SUPERVISOR_INFO = API_BASE_URL + "get_supervisor_info.php";
    public static final String API_GET_STUDENT_SCHEDULE = API_BASE_URL + "get_student_schedule.php";
    public static final String API_GET_TRAINER_INFO = API_BASE_URL + "get_trainer_info.php";
    public static final String API_GET_TRAINER_SCHEDULE = API_BASE_URL + "get_trainer_schedule.php";
    public static final String API_GET_TRAINER_TRAINEE_LIST = API_BASE_URL + "get_trainee_list_for_trainer.php";
    public static final String API_GET_TRAINEE_INFO = API_BASE_URL + "get_trainee_info_for_trainer.php";
    public static final String API_GET_TRAINEE_SCHEDULE_FOR_TRAINER = API_BASE_URL + "get_trainee_schedule_for_trainer.php";
    public static final String API_ADD_TRAINEE_SCHEDULE = API_BASE_URL + "add_trainee_schedule_for_trainer.php";
    public static final String API_DELETE_TRAINEE_SCHEDULE = API_BASE_URL + "delete_trainee_schedule_for_trainer.php";
    public static final String API_EDIT_TRAINEE_SCHEDULE = API_BASE_URL + "edit_trainee_schedule_for_trainer.php";
    public static final String API_GET_TRAINEE_SCHEDULE_INFO = API_BASE_URL + "get_trainee_schedule_info.php";
}
