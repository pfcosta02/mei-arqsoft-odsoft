package pt.psoft.g1.psoftg1.shared.model;

public class ForbiddenName
{
    private String forbiddenName;

    public ForbiddenName(String forbiddenName)
    {
        setForbiddenName(forbiddenName);
    }

    protected ForbiddenName()
    {
        // for frameworks if needed
    }

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
}
