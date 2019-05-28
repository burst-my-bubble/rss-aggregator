public interface TextAnalyser {
    String getSentiment(Pages pages) throws Exception;
    String getEntities(Pages pages) throws Exception;
}