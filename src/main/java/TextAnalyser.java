public interface TextAnalyser {
    String getSentiment(Pages pages);
    String getEntities(Pages pages);
}