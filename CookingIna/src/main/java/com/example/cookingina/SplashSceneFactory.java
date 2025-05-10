package com.example.cookingina;

import com.almasb.fxgl.app.scene.FXGLMenu;
import com.almasb.fxgl.app.scene.LoadingScene;
import com.almasb.fxgl.app.scene.MenuType;
import com.almasb.fxgl.app.scene.SceneFactory;

public class SplashSceneFactory extends SceneFactory {

    @Override
    public FXGLMenu newMainMenu() {
        return new MainMenu(); // Make sure this matches your actual main menu class
    }

    @Override
    public FXGLMenu newGameMenu() {
        return new CustomGameMenu(MenuType.GAME_MENU);
    }
}
