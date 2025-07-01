package Api;

import com.google.gson.JsonObject;

public class Types {
    public static class ApiQuery {
        private final String method;
        private final Object[] args;

        ApiQuery(String method, Object[] args) {
            this.method = method;
            this.args = args;
        }

        public String getMethod() {
            return method;
        }

        public Object[] getArgs() {
            return args;
        }
    }


    public static class ApiError extends Exception {
        public ApiError(String message) {
            super(message);
        }

        public static ApiError buildMsg(String msg, String realMsg) {
            JsonObject errorJson = new JsonObject();
            errorJson.addProperty("ErrorMessage", msg);
            errorJson.addProperty( "RealError", realMsg);

            return new ApiError(errorJson.getAsString());
        }
    }
}