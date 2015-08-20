package gladun.vladimir.nzdrivingtest;

/**
 * Category entity
 *
 * @author Vladimir Gladun vvgladoun@gmail.com
 */
public class Category {
    private int id;
    private String name;
    private int test_id;

    public Category(int id, String name, int test_id) {
        this.id = id;
        this.name = name;
        this.test_id = test_id;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
