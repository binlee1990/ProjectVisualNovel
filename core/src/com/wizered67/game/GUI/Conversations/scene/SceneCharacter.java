package com.wizered67.game.GUI.Conversations.scene;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.wizered67.game.GUI.Conversations.CompleteEvent;
import com.wizered67.game.GameManager;

/**
 * Represents a character in the game. Contains information about the
 * character's sprite, animation, name, and speaking sound.
 * @author Adam Victor
 */
public class SceneCharacter extends SceneEntity {
    private String identifier;
    /** TextureRegion to be drawn for the current frame of animation. */
    private transient TextureRegion currentSprite;
    /** Animation object containing all frames of animation. */
    private transient Animation currentAnimation;
    /** The name of the current animation. Used to tell if the animation is being changed. */
    private String animationName;
    /** Vector2 containing the position of the sprite to be drawn. */
    private final Vector2 position = new Vector2();
    /** Whether the Animation should loop. */
    private boolean looping;
    /** The stateTime used by Animation object to determine which frame should be displayed. */
    private float stateTime;
    /** The SceneManager containing this sprite and all others. */
    private SceneManager manager;
    /** Whether the animation being displayed has been finished. */
    private boolean wasFinished;
    /** Whether the character this SceneCharacter represents is currently speaking. */
    private boolean isSpeaking;
    /** The name to be displayed for the character. */
    private String knownName;
    /** The sound to be used when the character is speaking. */
    private String speakingSound;
    /** The Sprite object used to draw frames of animation. */
    private transient Sprite sprite;
    /** The default speaking sound for all characters. */
    public static final String DEFAULT_SPEAKING_SOUND = "talksoundmale.wav";
    /** How much the sprite drawn should be scaled. */
    private Vector2 scale;
    /** How much the alpha of the sprite being drawn should change per second. */
    private float fadePerSecond;
    /** Current Color of the sprite. Used to save data. */
    private Color color;

    /** No argument constructor */
    public SceneCharacter() {
        sprite = new Sprite();
        identifier = "";
    }
    /** Creates a SceneCharacter with the default speaking sound. */
    public SceneCharacter(SceneManager m) {
        this("", m, DEFAULT_SPEAKING_SOUND);
    }
    /** Creates a SceneCharacter with id ID and speaking sound SOUND. */
    public SceneCharacter(String id, SceneManager m, String sound) {
        identifier = id;
        manager = m;
        looping = false;
        stateTime = 0;
        wasFinished = false;
        isSpeaking = false;
        animationName = "";
        if (sound != null) {
            speakingSound = sound;
        } else {
            speakingSound = DEFAULT_SPEAKING_SOUND;
        }
        sprite = new Sprite();
        sprite.setAlpha(0);
        scale = new Vector2(2, 2);
        //sprite.setScale(2, 2);
    }
    /** Stores variables to save important information. */
    public void save() {
        if (sprite != null) {
            color = sprite.getColor();
        }
    }
    /** Reloads SceneCharacter. */
    public void reload() {
        currentAnimation = GameManager.assetManager().getAnimation(animationName);
        updateSprite();
        sprite.setColor(color);
        //sprite.setScale(scale.x, scale.y);
    }
    /** Returns the name of this SceneCharacter's speaking sound. */
    public String getSpeakingSound() {
        return speakingSound;
    }
    /** Sets this SceneCharacter's speaking sound to NEWSOUND. */
    public void setSpeakingSound(String newSound) {
        speakingSound = newSound;
    }
    /** Returns the display name of the character represented by this SceneCharacter. */
    public String getKnownName() {
        return knownName;
    }
    /** Sets the display name of this character to NEWNAME. */
    public void setKnownName(String newName) {
        knownName = newName;
    }

    /** Switches this SceneCharacter's animation to the one named NAME. Returns true if animation is valid. */
    public boolean setCurrentAnimation(String name) {
        if (!animationName.equalsIgnoreCase(name)) {
            stateTime = 0;
            animationName = name;
        }
        currentAnimation = GameManager.assetManager().getAnimation(name);
        wasFinished = false;
        if (currentAnimation == null) {
            GameManager.error("No animation found: " + name);
        }
        return currentAnimation != null;
    }
    /** Returns the name of this SceneCharacter's current Animation. */
    public String getAnimationName() {
        return animationName;
    }

    /** Updates the stateTime of the current Animation by DELTA STATE TIME and
     * updates the sprite to the Animation's current sprite. If the Animation is
     * completed it alerts the SceneManager. Also updates fading in or out the sprite.
     */
    public void updateAnimation(float deltaStateTime) {
        if (fadePerSecond != 0) {
            float alpha = sprite.getColor().a;
            alpha += deltaStateTime * fadePerSecond;
            sprite.setAlpha(alpha);
            if (alpha <= 0) {
                setFullVisible(false);
            } else if (alpha >= 1) {
                setFullVisible(true);
            }
        }
        if (isVisible() && currentAnimation != null) {
            stateTime += deltaStateTime;
            updateSprite();
            //sprite.setCenter(sprite.getWidth() * scale / 2, 0);
            if (currentAnimation.isAnimationFinished(stateTime) && !wasFinished) {
                manager.complete(CompleteEvent.animationEnd(animationName));
                wasFinished = true;
            }

        }
    }
    /** Updates the sprite to the correct animation frame. */
    private void updateSprite() {
        if (currentAnimation != null) {
            currentSprite = currentAnimation.getKeyFrame(stateTime, looping);
            sprite.setTexture(currentSprite.getTexture());
            sprite.setRegion(currentSprite);
            sprite.setBounds(position.x, position.y, currentSprite.getRegionWidth() * scale.x, currentSprite.getRegionHeight() * scale.y);
            sprite.setOrigin(Math.abs(sprite.getWidth()) / 2, 0);
        }
    }
    /** Draws this SceneCharacter's current sprite to the BATCH. */
    public void draw(Batch batch) {
        if (!isVisible() || currentSprite == null) {
            return;
        }
        sprite.draw(batch);
        /*
        batch.draw(currentSprite, position.x + (direction == -1 ? currentSprite.getRegionWidth() * scale : 0), position.y,
                currentSprite.getRegionWidth() * scale * direction,
                currentSprite.getRegionHeight() * scale);
                */
    }
    /** Sets this SceneCharacter's current sprite to the TextureRegion TEXTURE. */
    public void setCurrentSprite(TextureRegion texture) {
        currentSprite = texture;
        sprite.setTexture(currentSprite.getTexture());
        sprite.setRegion(currentSprite);
        sprite.setBounds(position.x, position.y, currentSprite.getRegionWidth(), currentSprite.getRegionHeight());
        sprite.setOrigin(sprite.getWidth() / 2, 0);
    }
    /** Sets current position to NEW POSITION. */
    public void setPosition(Vector2 newPosition){
        position.set(newPosition);
    }
    /** Sets current x to NEWX and current y to NEWY. */
    public void setPosition(float newX, float newY) {
        position.set(newX, newY);
    }
    /** Returns this SceneCharacter's current position. */
    public Vector2 getPosition() {
        return position;
    }
    /** Sets this SceneCharacter's visibility to VISIBLE. */
    public void setFullVisible(boolean visible) {
        if (fadePerSecond != 0) {
            manager.complete(CompleteEvent.fade(manager, this));
        }
        if (visible) {
            sprite.setAlpha(1);
        } else {
            sprite.setAlpha(0);
            removeFromScene();
        }
        fadePerSecond = 0;
    }
    /** Sets speaking status to SPEAKING. */
    public void setSpeaking(boolean speaking) {
        isSpeaking = speaking;
    }
    /** Sets the direction of the sprite to DIRECTION. */
    public void setDirection(int direction) {
        //sprite.setScale(Math.abs(sprite.getScaleX()) * direction, sprite.getScaleY());
        scale.x = Math.abs(scale.x) * direction;
    }
    /** Sets how fast the sprite should fade in or out. FADEAMOUNT is alpha per second. */
    public void setFade(float fadeAmount) {
        fadePerSecond = fadeAmount;
    }
    /** Whether this sprite is visible (ie alpha is not 0). */
    public boolean isVisible() {
        return sprite.getColor().a > 0;
    }
    /** Returns the Color of the sprite, including alpha. */
    public Color getColor() {
        if (sprite == null) {
            return null;
        }
        return sprite.getColor();
    }
    /** Sets the SceneManager for this SceneCharacter to SM and adds the SceneCharacter to that scene. */
    public void addToScene(SceneManager sm) {
        manager = sm;
        manager.addCharacter(identifier);
    }
    /** Removes this SceneCharacter from the SceneManager. */
    public void removeFromScene() {
        if (manager != null) {
            manager.removeCharacter(identifier);
            manager = null;
        }
    }
    @Override
    public int hashCode() {
        return identifier.hashCode();
    }
    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof SceneCharacter) {
            return ((SceneCharacter) o).identifier.equals(identifier);
        }
        return false;
    }
}