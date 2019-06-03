public class Entity {
    private String actualName;
    private String displayName;
    private String category;

    public Entity(String name, String category) {
        this.actualName = name;
        this.displayName = name;
        this.category = category;
    }

    public String getActualName() {
        return actualName;
    }

    public void setActualName(String actualName) {
        this.actualName = actualName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public boolean isOrganization() {
        return category.equals("Organization");
    }

    @Override
    public String toString() {
        return "(" + actualName + ", " + displayName + ", " + category + ")";
    }
}