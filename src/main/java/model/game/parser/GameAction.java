package model.game.parser;

public class GameAction {
    private String methodName;
    private String parameters;

    //Cancel default instance creation for Builder Pattern
    private GameAction() {
    }

    public static class newBuilder {
        private final GameAction GAR;

        public newBuilder(String methodName, String sessionID) {
            GAR = new GameAction();
            GAR.methodName = "<methodName>" + methodName + "</methodName>";
            GAR.parameters = "<param> <value> <string>" + sessionID + "</string> </value> </param>";
        }

        public newBuilder addParameter(String parameter) {
            GAR.parameters += "<param> <value> <string>" + parameter + "</string> </value> </param>";
            return this;
        }

        public newBuilder addParameter(int parameter) {
            GAR.parameters += "<param> <value> <int>" + parameter + "</int> </value> </param>";
            return this;
        }

        public GameAction build() {
            return GAR;
        }
    }

    public String getRequest() {
        return "<methodCall>" + methodName + "<params>" + parameters + "</params>" + "</methodCall>";
    }

}
