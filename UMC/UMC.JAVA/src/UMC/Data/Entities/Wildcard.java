package UMC.Data.Entities;

public class Wildcard {
    public String WildcardKey; 
    public String GroupBy;
    public String Description;
    public String Authorizes;

    public Wildcard setWildcardKey(String wildcardKey) {
        WildcardKey = wildcardKey;
        return this;
    }

    public Wildcard setGroupBy(String groupBy) {
        GroupBy = groupBy;
        return this;
    }

    public Wildcard setDescription(String description) {
        Description = description;
        return this;
    }

    public Wildcard setAuthorizes(String authorizes) {
        Authorizes = authorizes;
        return this;
    }
}
