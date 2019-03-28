package sudoku;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.remote.RemoteWebDriver;

public class Main {

	static int[][] grid;
	static boolean[][] vis;
	static int dim = 9;
	
	public static void main(String[] args) {
		grid = new int[dim][dim];
		vis = new boolean[dim][dim];
		String homeDir = System.getProperty("user.home");
		System.setProperty("webdriver.chrome.driver", homeDir+"/chromedriver.exe");
		RemoteWebDriver web = new ChromeDriver();
		web.get("https://nine.websudoku.com/?level=4");
		WebElement table = web.findElement(By.id("puzzle_grid"));
		List<WebElement> inputs = table.findElements(By.tagName("INPUT"));
		for (int i = 0; i < dim; i++) {
			for (int j = 0; j < dim; j++) {
				String content = inputs.get(i*dim + j).getAttribute("VALUE");
				if(content == null)
					grid[i][j] = 0;
				else
					grid[i][j] = Integer.parseInt(content);
				if(grid[i][j] != 0)
					vis[i][j] = true;
			}
		}
		
		solve(0, 0);
		for (int i = 0; i < dim; i++) {
			for (int j = 0; j < dim; j++) {
				if(!vis[i][j])
					setAttribute(inputs.get(i*dim + j), "VALUE", grid[i][j]+"",web);
			}
		}
		
		WebElement submit = web.findElement(By.xpath("//input[@name='submit']"));
		submit.click();
		
	}
	
	public static void setAttribute(WebElement element, String attName, String attValue,RemoteWebDriver  driver) {
        driver.executeScript("arguments[0].setAttribute(arguments[1], arguments[2]);", 
                element, attName, attValue);
    }
	
	static boolean check(int x, int y, int choix) {
		// check la ligne
		for (int i = 0; i < dim; i++) {
			if (i != y) {
				if (grid[x][i] == choix)
					return false;
			}
		}
		// check la colonne
		for (int i = 0; i < dim; i++) {
			if (i != x) {
				if (grid[i][y] == choix)
					return false;
			}
		}

		// chcek le carré
		int rac = (int) Math.sqrt(dim);

		int a = (x / rac) * rac;
		int b = (y / rac) * rac;

		for (int i = a; i < a + rac; i++) {
			for (int j = b; j < b + rac; j++) {
				if (i == x && j == y)
					continue;
				if (grid[i][j] == choix) {
					return false;
				}
			}
		}

		return true;
	}

	public static boolean solve(int row, int col) {
		if (col == dim)
			return true;
		
		if (row == dim)
			return solve(0, col + 1);

		if (vis[row][col])
			return solve(row + 1, col);

		for (int i = 1; i <= dim; i++) {
			if (check(row, col, i)) {
				grid[row][col] = i;
				if (solve(row + 1, col)){
					return true;
				}
				grid[row][col] = 0;
			}
		}

		return false;
	}
}
