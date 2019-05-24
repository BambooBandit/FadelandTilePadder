package com.fadeland.tilepadder;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.PixmapIO;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Json;

import java.util.Scanner;

public class TilePadder extends ApplicationAdapter {
	String[] args;
	Pixmap tilesheet;
	Pixmap paddedTilesheet;
	int tileSize;
	int padAmount;
	boolean expandXPOT;
	boolean expandYPOT;

	public TilePadder(String[] args){
		this.args = args;
	}

	@Override
	public void create () {
	    if(args.length != 3){
            System.out.println("Wrong amount of arguments. Usage is [inputPath] [outputPath] [name].");
            Gdx.app.exit();
            System.exit(0);
        }
		String inputPath = args[0];
		String outputPath = args[1];
		String name = args[2];
        FileHandle inputPathFileHandle = Gdx.files.internal(inputPath);

        FileHandle settingsFile = new FileHandle(inputPathFileHandle.path() + "\\settings.json");
        String content = new Scanner(settingsFile.read()).useDelimiter("\\Z").next();
        Json json = new Json();
        Settings settings = json.fromJson(Settings.class, content);

        this.tileSize = settings.tileSize;
        this.padAmount = settings.padAmount;
        this.expandXPOT = settings.expandXPOT;
        this.expandYPOT = settings.expandYPOT;

		this.tilesheet = new Pixmap(Gdx.files.internal(inputPath + "\\" + name + ".png"));

		// Determine the size of the new sheet
		int tileColumnAmount = getTileColumnAmount(tilesheet, tileSize);
		int tileRowAmount = getTileRowAmount(tilesheet, tileSize);
		int paddedTileSheetWidthPrediction = (tileColumnAmount * tileSize) + tileColumnAmount * (padAmount * 2);
		int paddedTileSheetHeightPrediction = (tileRowAmount * tileSize) + tileRowAmount * (padAmount * 2);
		if(paddedTileSheetWidthPrediction > tilesheet.getWidth() || paddedTileSheetHeightPrediction > tilesheet.getHeight()){
			int powerOfTwoWidth = MathUtils.nextPowerOfTwo(tilesheet.getWidth());
			if(!this.expandXPOT)
			    powerOfTwoWidth = paddedTileSheetWidthPrediction;
			int powerOfTwoHeight = MathUtils.nextPowerOfTwo(tilesheet.getHeight());
            if(!this.expandYPOT)
                powerOfTwoHeight = paddedTileSheetHeightPrediction;
			this.paddedTilesheet = new Pixmap(powerOfTwoWidth, powerOfTwoHeight, tilesheet.getFormat());
		}
		else {
			this.paddedTilesheet = new Pixmap(tilesheet.getWidth(), tilesheet.getHeight(), tilesheet.getFormat());
		}

		addTilesToPaddedSheet(tilesheet, paddedTilesheet, tileSize, padAmount);

		fillEmptyPadSpace(paddedTilesheet, tileSize, padAmount);

		PixmapIO.writePNG(new FileHandle(outputPath + "/" + name + ".png"), paddedTilesheet);
		Gdx.app.exit();
		System.exit(0);
	}

    private void fillEmptyPadSpace(Pixmap paddedTilesheet, int tileSize, int padAmount) {
        int row = 0;
        for(int y = 0; y < paddedTilesheet.getHeight(); y += tileSize + (padAmount * 2)){
            fillEmptyPadSpaceHorizontal(paddedTilesheet, row, tileSize, padAmount);
            row ++;
        }

        int col = 0;
        for(int x = 0; x < paddedTilesheet.getWidth(); x += tileSize + (padAmount * 2)){
            fillEmptyPadSpaceVertical(paddedTilesheet, col, tileSize, padAmount);
            col ++;
        }
    }

    private void fillEmptyPadSpaceHorizontal(Pixmap paddedTilesheet, int row, int tileSize, int padAmount){
        int tileTopY = padAmount;
        int tileBottomY = padAmount + tileSize - 1;
        for(int i = 0; i < row; i ++) {
            tileTopY += (tileSize + (padAmount * 2));
            tileBottomY += (tileSize + (padAmount * 2));
        }
        for(int x = 0; x < paddedTilesheet.getWidth(); x++){
            int pixelColorToDupe = paddedTilesheet.getPixel(x, tileTopY);
            paddedTilesheet.setColor(pixelColorToDupe);
            for(int y = tileTopY - 1; y < tileTopY - 1 + padAmount; y++){
                paddedTilesheet.drawPixel(x, y);
            }
            pixelColorToDupe = paddedTilesheet.getPixel(x, tileBottomY);
            paddedTilesheet.setColor(pixelColorToDupe);
            for(int y = tileBottomY + 1; y > tileBottomY + 1 - padAmount; y--){
                paddedTilesheet.drawPixel(x, y);
            }
        }
    }

    private void fillEmptyPadSpaceVertical(Pixmap paddedTilesheet, int col, int tileSize, int padAmount){
        int tileLeftX = padAmount;
        int tileRightX = padAmount + tileSize - 1;
        for(int i = 0; i < col; i ++) {
            tileLeftX += (tileSize + (padAmount * 2));
            tileRightX += (tileSize + (padAmount * 2));
        }
        for(int y = 0; y < paddedTilesheet.getHeight(); y++){
            int pixelColorToDupe = paddedTilesheet.getPixel(tileLeftX, y);
            paddedTilesheet.setColor(pixelColorToDupe);
            for(int x = tileLeftX - 1; x < tileLeftX - 1 + padAmount; x++){
                paddedTilesheet.drawPixel(x, y);
            }
            pixelColorToDupe = paddedTilesheet.getPixel(tileRightX, y);
            paddedTilesheet.setColor(pixelColorToDupe);
            for(int x = tileRightX + 1; x > tileRightX + 1 - padAmount; x--){
                paddedTilesheet.drawPixel(x, y);
            }
        }
    }

	private void addTilesToPaddedSheet(Pixmap tilesheet, Pixmap paddedTilesheet, int tileSize, int padAmount) {
		int padX = padAmount;
		int padY = padAmount;
		// iterate through tiles, iterate through pixels
		for(int tileX = 0; tileX < tilesheet.getWidth(); tileX += tileSize){
			for(int tileY = 0; tileY < tilesheet.getHeight(); tileY+= tileSize){
				handleTileBeingAddedToTileSheet(tileX, tileY, padX, padY);
				padY += padAmount * 2;
			}
			padY = padAmount;
			padX += padAmount * 2;
		}
	}

	private void handleTileBeingAddedToTileSheet(int tileX, int tileY, int padX, int padY){
		for(int pixelX = tileX; pixelX < tileX + tileSize; pixelX ++){
			for(int pixelY = tileY; pixelY < tileY + tileSize; pixelY ++){
				int tileSheetPixelColor = tilesheet.getPixel(pixelX, pixelY);
				paddedTilesheet.setColor(tileSheetPixelColor);
				paddedTilesheet.drawPixel(pixelX + padX, pixelY + padY);
			}
		}
	}

	/** Returns the amount of tiles that take up the sheet horizontally. Assumes that tiles start from the very left. */
	private int getTileColumnAmount(Pixmap tilesheet, int tileSize){
		int maxTileWidth = 0;
		for(int y = 0; y < tilesheet.getHeight(); y += tileSize) {
			for (int x = 0; x < tilesheet.getWidth(); x += tileSize) {
				if ((tilesheet.getPixel(x, y) & 255) != 0) { // Not a completely transparent pixel
					if(maxTileWidth < x)
						maxTileWidth = x;
				}
			}
		}

		int tileColumnAmount = (maxTileWidth + tileSize) / tileSize;
		return tileColumnAmount;
	}

	/** Returns the amount of tiles that take up the sheet vertically. Assumes that tiles start from the very top. */
	private int getTileRowAmount(Pixmap tilesheet, int tileSize){
		int maxTileHeight = 0;
		for(int x = 0; x < tilesheet.getWidth(); x += tileSize) {
			for (int y = 0; y < tilesheet.getHeight(); y += tileSize) {
				if ((tilesheet.getPixel(x, y) & 255) != 0) { // Not a completely transparent pixel
					if(maxTileHeight < y)
						maxTileHeight = y;
				}
			}
		}

		int tileRowAmount = (maxTileHeight + tileSize) / tileSize;
		return tileRowAmount;
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
	}
	
	@Override
	public void dispose () {
		tilesheet.dispose();
		paddedTilesheet.dispose();
	}
}
