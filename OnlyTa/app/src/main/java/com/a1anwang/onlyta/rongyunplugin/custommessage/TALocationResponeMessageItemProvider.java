package com.a1anwang.onlyta.rongyunplugin.custommessage;

import android.content.Context;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.a1anwang.onlyta.R;
import com.a1anwang.onlyta.util.LogUtils;

import io.rong.imkit.model.ProviderTag;
import io.rong.imkit.model.UIMessage;
import io.rong.imkit.widget.provider.IContainerItemProvider;
import io.rong.imlib.model.Message;

/**
 * Created by a1anwang.com on 2018/1/24.
 */
@ProviderTag(messageContent = TALocationResponeMessage.class)
public class TALocationResponeMessageItemProvider extends IContainerItemProvider.MessageProvider<TALocationResponeMessage> {
    class ViewHolder {
        TextView tv_content;
    }

    @Override
    public void bindView(View view, int i, TALocationResponeMessage taLocationResponeMessage, UIMessage message) {
        ViewHolder holder = (ViewHolder) view.getTag();

        if (message.getMessageDirection() == Message.MessageDirection.SEND) {//消息方向，自己发送的
            holder.tv_content.setBackgroundResource(R.drawable.bg_round_solid_blue_15dp);
            holder.tv_content.setTextColor(Color.WHITE);
        } else {
            holder.tv_content.setBackgroundResource(R.drawable.bg_round_solid_light_gray_15dp);
            holder.tv_content.setTextColor(Color.DKGRAY);
        }

        String content=taLocationResponeMessage.getContent();

        LogUtils.e(LogUtils.TAG_1," content:"+taLocationResponeMessage.getContent() +" "+taLocationResponeMessage.toString());
        if(content!=null){
            holder.tv_content.setText(content);

        }
    }

    @Override
    public Spannable getContentSummary(TALocationResponeMessage taLocationRequestMessage) {
        return new SpannableString("[请求查看位置信息]");
    }

    @Override
    public void onItemClick(View view, int i, TALocationResponeMessage taLocationRequestMessage, UIMessage uiMessage) {

    }

    @Override
    public View newView(Context context, ViewGroup viewGroup) {
        View view = LayoutInflater.from(context).inflate(io.rong.imkit.R.layout.rc_item_message_custom, null);
        ViewHolder holder = new ViewHolder();
        holder.tv_content = (TextView) view.findViewById(io.rong.imkit.R.id.rc_msg);
        view.setTag(holder);
        return view;
    }
}
