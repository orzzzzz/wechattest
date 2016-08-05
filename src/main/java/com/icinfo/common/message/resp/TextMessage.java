package com.icinfo.common.message.resp;

/**
 * 文本消息
 *
 * @author liufeng
 * @date 2013-09-11
 */
public class TextMessage extends BaseMessage {
    /**
     * 文本消息内容
     */
    public String Content;
    /**
     * 消息id，64位整型
     */
    public long MsgId ;

    public String getContent() {
        return Content;
    }
    public void setContent(String content) {
        Content = content;
    }
    public long getMsgId() {
        return MsgId;
    }
    public void setMsgId(long msgId) {
        MsgId = msgId;
    }

    @Override
    public String toString() {
        return "TextMessage{" +
                "Content='" + Content + '\'' +
                ", MsgId=" + MsgId +
                '}';
    }
}

