package creature;

import java.util.Objects;

import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * Displays the correct pose from a cat's sprite sheet.
 *  *
 * @author Sahtra Green
 * @version 0.1.0
 * @since 8/11/2026
 */

public final class CatSpriteRenderer {

    public static final int IDLE = 0;
    public static final int BATTLE_OPPONENT = 1;
    public static final int BATTLE_PLAYER = 2;

    private static final int FRAME_COUNT = 3;

    private CatSpriteRenderer() {
        // Utility class.
    }

    public static void setSprite(
            ImageView imageView,
            Cat cat,
            int frameIndex
    ) {
        Image spriteSheet =
                new Image(
                        Objects.requireNonNull(
                                CatSpriteRenderer.class
                                        .getResourceAsStream(
                                                cat.getSpriteSheetPath()
                                        )
                        )
                );

        double frameWidth =
                spriteSheet.getWidth() / FRAME_COUNT;

        imageView.setImage(
                spriteSheet
        );

        imageView.setViewport(
                new Rectangle2D(
                        frameWidth * frameIndex,
                        0,
                        frameWidth,
                        spriteSheet.getHeight()
                )
        );

        imageView.setPreserveRatio(true);
        imageView.setSmooth(false);
    }
}