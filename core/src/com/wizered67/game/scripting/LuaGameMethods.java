package com.wizered67.game.scripting;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.MathUtils;
import com.wizered67.game.GameManager;
import com.wizered67.game.conversations.ConversationController;
import com.wizered67.game.conversations.scene.CharacterDefinition;
import com.wizered67.game.conversations.scene.SceneManager;
import com.wizered67.game.gui.GUIManager;
import org.luaj.vm2.LuaValue;

/**
 * Contains methods that can be called in Lua scripts using 'game:'.
 * @author Adam Victor
 */
public class LuaGameMethods {
    public static int randomRange(int lowerBound, int upperBound) {
        return MathUtils.random(lowerBound, upperBound);
    }

    public static void printAll(int[] nums) {
        for (int i : nums) {
            System.out.println(i);
        }
    }

    public static void addToTranscript(String speaker, String phrase) {
        GameManager.conversationController().getTranscript().addMessage(speaker, phrase);
    }

    public static void openTranscript() {
        GameManager.guiManager().toggleTranscript();
    }

    public static void getNameInput(final String variableName) {
        ConversationController.scriptManager("Lua").setValue(variableName, LuaValue.NIL);
        Input.TextInputListener inputListener = new Input.TextInputListener() {
            @Override
            public void input(String text) {
                ConversationController.scriptManager("Lua").setValue(variableName, text);
            }

            @Override
            public void canceled() {
                getNameInput(variableName);
            }
        };
        Gdx.input.getTextInput(inputListener, "Enter your name", "", "");
    }

    public static void setCharacterName(String character, String name) {
        CharacterDefinition definition = SceneManager.getCharacterDefinition(character);
        if (definition != null) {
            definition.setKnownName(name);
        }
    }
}
