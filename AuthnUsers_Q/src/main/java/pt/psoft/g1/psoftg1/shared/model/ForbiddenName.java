package pt.psoft.g1.psoftg1.shared.model;

public class ForbiddenName 
{
    private String id;
    private String forbiddenName;

    public ForbiddenName(String forbiddenName) 
    {
        setForbiddenName(forbiddenName);
    }

    protected ForbiddenName() 
    {
        // for frameworks if needed
    }

    public void setId(String id) { this.id = id; }
    private void setForbiddenName(String forbiddenName) 
    {
        if (forbiddenName == null || forbiddenName.isBlank()) 
        {
            throw new IllegalArgumentException("Forbidden name cannot be null or blank");
        }

        this.forbiddenName = forbiddenName;
    }

    public String getForbiddenName() 
    {
        return forbiddenName;
    }

    public String getId() { return id; }
}