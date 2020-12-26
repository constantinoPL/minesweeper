package com.javarush.games.minesweeper;

import com.javarush.engine.cell.Color;
import com.javarush.engine.cell.Game;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MinesweeperGame extends Game {
    private static final int SIDE = 9;
    private GameObject[][] gameField = new GameObject[SIDE][SIDE];
    private int countMinesOnField;
    private int countFlags;
    private int countClosedTiles = SIDE * SIDE;
    private int score;
    private static final String MINE = "\uD83D\uDCA3";
    private static final String FLAG = "\uD83D\uDEA9";
    private boolean isGameStopped;

    @Override
    public void initialize() {
        setScreenSize(SIDE, SIDE);
        createGame();
    }
    private void createGame() {
        for (int y = 0; y < SIDE; y++) {
            for (int x = 0; x < SIDE; x++) {
                boolean isMine = getRandomNumber(10) < 1;
                if (isMine) {
                    countMinesOnField++;
                }
                gameField[y][x] = new GameObject(x, y, isMine);
                setCellColor(x, y, Color.ORANGE);
                setCellValue(x, y, "");
            }
        }
        countMineNeighbors();
        countFlags = countMinesOnField;
    }


    private void countMineNeighbors(){
        for (int y = 0; y < SIDE; y++) {
            for (int x = 0; x < SIDE; x++) {
                if(!gameField[y][x].isMine){
                    List<GameObject> minesQuantity = getNeighbors(gameField[y][x]);
                    Iterator<GameObject> iterator = minesQuantity.iterator();
                    while (iterator.hasNext()){
                        if(iterator.next().isMine) gameField[y][x].countMineNeighbors++;
                    }
                }
            }
        }
    }
    @Override
    public void onMouseLeftClick(int x, int y) {
        if(!isGameStopped) openTile(x, y);
        else restart();
    }

    @Override
    public void onMouseRightClick(int x, int y) {
        markTile(x, y);
    }


    private void markTile(int x, int y){
        if(!gameField[y][x].isOpen && !isGameStopped) {
            if(!gameField[y][x].isFlag && countFlags != 0){
                setCellValue(x, y, FLAG);
                setCellColor(x, y, Color.GREEN);
                gameField[y][x].isFlag = true;
                countFlags--;
            } else if(gameField[y][x].isFlag){
                setCellColor(x, y, Color.ORANGE);
                setCellValue(x, y, "");
                gameField[y][x].isFlag = false;
                countFlags++;
            }
        }
    }

    private void openTile(int x, int y) {
        if(!isGameStopped && !gameField[y][x].isOpen && !gameField[y][x].isFlag) {
            if (gameField[y][x].isMine) {
                setCellValueEx(x, y, Color.RED, MINE);
                gameOver();

            } else if (!gameField[y][x].isOpen) {
                if (gameField[y][x].countMineNeighbors != 0) {
                    setCellNumber(x, y, gameField[y][x].countMineNeighbors);
                    gameField[y][x].isOpen = true;
                    countClosedTiles--;
                    score += 5;
                    setScore(score);
                    setCellColor(x, y, Color.AQUA);
                }
                if (gameField[y][x].countMineNeighbors == 0) {
                    setCellValue(x, y, "");
                    gameField[y][x].isOpen = true;
                    countClosedTiles--;
                    score += 5;
                    setScore(score);
                    setCellColor(x, y, Color.AQUA);
                    List<GameObject> openIfNoMine = getNeighbors(gameField[y][x]);
                    Iterator<GameObject> iterator = openIfNoMine.iterator();
                    while (iterator.hasNext()) {
                        GameObject temp = iterator.next();
                        if (!gameField[temp.y][temp.x].isOpen)
                            openTile(temp.x, temp.y);
                    }
                }
            }
        }
        if(countClosedTiles == countMinesOnField) win();
    }



    private List<GameObject> getNeighbors(GameObject gameObject) {
        List<GameObject> result = new ArrayList<>();
        for (int y = gameObject.y - 1; y <= gameObject.y + 1; y++) {
            for (int x = gameObject.x - 1; x <= gameObject.x + 1; x++) {
                if (y < 0 || y >= SIDE) {
                    continue;
                }
                if (x < 0 || x >= SIDE) {
                    continue;
                }
                if (gameField[y][x] == gameObject) {
                    continue;
                }
                result.add(gameField[y][x]);
            }
        }
        return result;
    }

    private void gameOver(){
        isGameStopped = true;
        showMessageDialog(Color.RED, "Game Over",Color.BLACK,40);
    }
    private void win(){
        isGameStopped = true;
        showMessageDialog(Color.NONE, "You WIN!!!",Color.CORAL,40);
    }
    private void restart(){
        isGameStopped = false;
        countClosedTiles = SIDE * SIDE;
        score = 0;
        countMinesOnField = 0;
        setScore(score);
        createGame();
    }
}