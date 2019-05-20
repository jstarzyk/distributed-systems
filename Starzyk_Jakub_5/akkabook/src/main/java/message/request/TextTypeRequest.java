package message.request;

import message.response.BookText;

public class TextTypeRequest {

    private BookText.Type textType;

    public TextTypeRequest(BookText.Type textType) {
        this.textType = textType;
    }

    public BookText.Type getTextType() {
        return textType;
    }
}
