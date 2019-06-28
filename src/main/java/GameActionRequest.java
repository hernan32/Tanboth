public class GameActionRequest {
    private String methodName;
    private String parameters;

    //Cancel default instance creation for Builder Pattern
    private GameActionRequest() {
    }

    public static class newBuilder {
        private GameActionRequest GAR;

        public newBuilder(String methodName, String sessionID) {
            GAR = new GameActionRequest();
            GAR.methodName = "<methodName>" + methodName + "</methodName>";
            GAR.parameters = "<param> <value> <string>" + sessionID + "</string> </value> </param>";
        }

        public newBuilder addParameter(String parameter) {
            GAR.parameters += "<param> <value> <string>" + parameter + "</string> </value> </param>";
            return this;
        }

        public GameActionRequest build() {
            return GAR;
        }
    }

    public String getRequest() {
        return "<methodCall>" + methodName + "<params>" + parameters + "</params>" + "</methodCall>";
    }

}
