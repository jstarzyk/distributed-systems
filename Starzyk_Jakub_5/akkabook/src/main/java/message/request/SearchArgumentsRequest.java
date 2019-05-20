package message.request;

public class SearchArgumentsRequest implements scala.Serializable {

    public enum SearchType {

        CONTAINS,
        EQUALS

    }

    private final SearchType searchType;
    private final boolean matchCase;

    public SearchArgumentsRequest(SearchType searchType, boolean matchCase) {
        this.searchType = searchType;
        this.matchCase = matchCase;
    }

    public SearchType getSearchType() {
        return searchType;
    }

    public boolean isMatchCase() {
        return matchCase;
    }
}
