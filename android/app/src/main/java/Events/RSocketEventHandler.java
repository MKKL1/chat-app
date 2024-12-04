package Events;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import Data.Models.Message;
import Data.Models.Token;
import DataAccess.ViewModels.ChannelViewModel;
import DataAccess.ViewModels.CommunityViewModel;
import DataAccess.ViewModels.MessageViewModel;
import DataAccess.ViewModels.RoleViewModel;
import DataAccess.ViewModels.UserViewModel;

public class RSocketEventHandler {

    Activity activity;

    CommunityViewModel communityViewModel;
    UserViewModel userViewModel;
    RoleViewModel roleViewModel;
    ChannelViewModel channelViewModel;
    MessageViewModel messageViewModel;

    Token token = new Token();
    long communityId;
    long userId;
    /**
     *  Available events (01.12.2024):
     *
     *             "COMMUNITY_DELETE_EVENT",
     *             "COMMUNITY_UPDATE_EVENT",
     *
     *             "MEMBER_CREATE_EVENT",
     *             "MEMBER_DELETE_EVENT",
     *
     *             "CHANNEL_CREATE_EVENT",
     *             "CHANNEL_DELETE_EVENT",
     *             "CHANNEL_UPDATE_EVENT",
     *
     *             "MESSAGE_CREATE_EVENT",
     *             "MESSAGE_DELETE_EVENT",
     *             "MESSAGE_UPDATE_EVENT",
     *
     *             "REACTION_CREATE_EVENT",
     *             "REACTION_DELETE_EVENT",
     *
     *             "ROLE_CREATE_EVENT",
     *             "ROLE_DELETE_EVENT",
     *             "ROLE_UPDATE_EVENT",
     *
     *             "USER_UPDATE_EVENT",
     *
     *             "PARTICIPANT_CREATE_EVENT",
     *             "PARTICIPANT_DELETE_EVENT"
     */



    public RSocketEventHandler(Context context, String accessToken, long communityId, long userId) {
        if (!(context instanceof Activity)) {
            throw new IllegalArgumentException("Context must be an instance of Activity");
        }
        if (!(context instanceof ViewModelStoreOwner)) {
            throw new IllegalArgumentException("Context must implement ViewModelStoreOwner");
        }

        this.activity = (Activity) context;
        this.token.setAccessToken(accessToken);
        this.communityId = communityId;
        this.userId = userId;

        ViewModelProvider provider = new ViewModelProvider((ViewModelStoreOwner) context);
        this.communityViewModel = provider.get(CommunityViewModel.class);
        this.userViewModel = provider.get(UserViewModel.class);
        this.roleViewModel = provider.get(RoleViewModel.class);
        this.channelViewModel = provider.get(ChannelViewModel.class);
        this.messageViewModel = provider.get(MessageViewModel.class);
    }

    /**
     * Retrieve event type from RSocket stream and pass it to specific function
     * @param event - event message from server
     */
    public void handleEvent(String event){
        Log.d("RSocketEventHandler", "Otrzymano event: " + event);
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(event);
            String eventName = jsonNode.get("name").asText();
            String[] parts = eventName.split("_", 2);
            if (parts.length < 2) {
                handleUnknownEvent(eventName, event);
                return;
            }
//            MESSAGE_
            String category = parts[0];
//            CREATE_
            String action = parts[1];

            switch (category) {
                case "MESSAGE": handleMessageEvent(action, event); break;
                case "CHANNEL": handleChannelEvent(action, event); break;
                case "USER": handleUserEvent(action, event); break;
                case "COMMUNITY": handleCommunityEvent(action, event); break;
                case "MEMBER": handleMemberEvent(action, event); break;
                case "REACTION": handleReactionEvent(action, event); break;
                case "ROLE": handleRoleEvent(action, event); break;
                case "PARTICIPANT": handleParticipantEvent(action, event); break;
                default: handleUnknownEvent(eventName, event); break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Handle errors from server RSocket stream
     * @param error
     */
    public void handleError(Throwable error){
        Log.e("RSocketEventHandler", "Błąd subskrypcji", error);
    }

    /**
     * Handle MESSAGE _(ACTION)_EVENT
     * @param action part of event's message "name" after MESSAGE_
     * @param eventData - whole event's message
     */
    private void handleMessageEvent(String action, String eventData) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            Message message;
            JsonNode rootNode = objectMapper.readTree(eventData);
            JsonNode dataNode = rootNode.get("data");

            if (dataNode != null) {
                message = objectMapper.treeToValue(dataNode, Message.class);
            } else {
                throw new IllegalArgumentException("JSON nie zawiera sekcji 'data'");
            }
            switch (action) {
                case "CREATE_EVENT": messageViewModel.addMessage(message); break;
                case "DELETE_EVENT": messageViewModel.deleteMessage(message); break;
                case "UPDATE_EVENT": messageViewModel.updateMessage(message); break;
                default: handleUnknownAction("MESSAGE", action, eventData); break;
            }
        } catch (JsonProcessingException e) {
            Log.d("RSocketEventHandler", "Błąd parsowania Event data na Message" + e.getMessage());
        }
    }

    /**
     * Handle CHANNEL_(ACTION)_EVENT
     * @param action part of event's message "name" after CHANNEL
     * @param eventData - whole event's message
     */
    private void handleChannelEvent(String action, String eventData) {
        switch (action) {
            case "CREATE_EVENT":

                break;

            case "DELETE_EVENT":

                break;

            case "UPDATE_EVENT":

                break;

            default:
                handleUnknownAction("CHANNEL", action, eventData);
                break;
        }
    }

    /**
     * Handle USER_(ACTION)_EVENT
     * @param action part of event's message "name" after USER
     * @param eventData - whole event's message
     */
    private void handleUserEvent(String action, String eventData) {
        switch (action) {
            case "UPDATE_EVENT":

                break;

            default:
                handleUnknownAction("USER", action, eventData);
                break;
        }
    }

    /**
     * Handle COMMUNITY_(ACTION)_EVENT
     * @param action part of event's message "name" after COMMUNITY
     * @param eventData - whole event's message
     */
    private void handleCommunityEvent(String action, String eventData) {
        switch (action) {
            case "DELETE_EVENT":

                break;

            case "UPDATE_EVENT":

                break;

            default:
                handleUnknownAction("COMMUNITY", action, eventData);
                break;
        }
    }

    /**
     * Handle MEMBER_(ACTION)_EVENT
     * @param action part of event's message "name" after MEMBER
     * @param eventData - whole event's message
     */
    private void handleMemberEvent(String action, String eventData) {
        switch (action) {
            case "CREATE_EVENT":

                break;

            case "DELETE_EVENT":

                break;

            default:
                handleUnknownAction("MEMBER", action, eventData);
                break;
        }
    }

    /**
     * Handle REACTION_(ACTION)_EVENT
     * @param action part of event's message "name" after REACTION
     * @param eventData - whole event's message
     */
    private void handleReactionEvent(String action, String eventData) {
        switch (action) {
            case "CREATE_EVENT":

                break;

            case "DELETE_EVENT":

                break;

            default:
                handleUnknownAction("REACTION", action, eventData);
                break;
        }
    }

    /**
     * Handle ROLE_(ACTION)_EVENT
     * @param action part of event's message "name" after ROLE
     * @param eventData - whole event's message
     */
    private void handleRoleEvent(String action, String eventData) {
        switch (action) {
            case "CREATE_EVENT":

                break;

            case "DELETE_EVENT":

                break;

            case "UPDATE_EVENT":

                break;

            default:
                handleUnknownAction("ROLE", action, eventData);
                break;
        }
    }

    /**
     * Handle PARTICIPANT_(ACTION)_EVENT
     * @param action part of event's message "name" after PARTICIPANT
     * @param eventData - whole event's message
     */
    private void handleParticipantEvent(String action, String eventData) {
        switch (action) {
            case "CREATE_EVENT":

                break;

            case "DELETE_EVENT":

                break;

            default:
                handleUnknownAction("PARTICIPANT", action, eventData);
                break;
        }
    }

    /**
     * MESSAGE_ - first part
     * CREATE_EVENT - second part
     * Handle unknown "name" part
     * @param name - first part of event's message
     * @param eventData - whole event's message
     */
    private void handleUnknownEvent(String name, String eventData) {
        Log.d("RSocketEventHandler", "Nieznane zdarzenie: " + name + "\ndane: " + eventData);
    }

    /**
     * MESSAGE_ - first part
     * CREATE_EVENT - second part
     * Handle unknown event action,
     * @param category - first part of event's message "name"
     * @param action - second part
     * @param eventData - whole event's message
     */
    private void handleUnknownAction(String category, String action, String eventData) {
        Log.d("RSocketEventHandler", "Nieznana akcja " + category + ": " + action + "\ndane: " + eventData);
    }

}

