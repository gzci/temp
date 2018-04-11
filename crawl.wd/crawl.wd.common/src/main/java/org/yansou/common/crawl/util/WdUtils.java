package org.yansou.common.crawl.util;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Stream;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.yansou.common.util.SleepUtils;

import com.google.common.base.Function;

public class WdUtils {
	final static public Optional<WebElement> cssOptional(SearchContext wd, String selector) {
		try {
			WebElement elemenet = wd.findElement(By.cssSelector(selector));
			return Optional.ofNullable(elemenet);
		} catch (NoSuchElementException e) {
			Logs.debug(e);
		}
		return Optional.empty();
	}

	final static public WebElement waitCssSelector(WebDriver wd, String selector, long waitTime) {
		WebDriverWait wdw = new WebDriverWait(wd, waitTime);
		return wdw.until(new Function<WebDriver, WebElement>() {
			@Override
			public WebElement apply(WebDriver webDriver) {
				return webDriver.findElement(By.cssSelector(selector));
			}
		});
	}

	final static public void waitPageLoad(WebDriver wd, long beforeSleepTime) {
		SleepUtils.sleep(beforeSleepTime);
		waitPageLoad(wd);
	}

	/**
	 * 
	 * @param wd
	 */
	final static public void waitPageLoad(WebDriver wd) {
		Function<WebDriver, Boolean> un = (wwd) -> ((JavascriptExecutor) wwd)
				.executeScript("return document.readyState").equals("complete");
		for (;;) {
			WebDriverWait wait = new WebDriverWait(wd, 300);
			wait.until(un);
			if (un.apply(wd)) {
				break;
			}
		}
	}

	@SafeVarargs
	final static public Stream<WebElement> waitFindByCss(WebDriver wd, String cssQuery, long waitTime,
			Consumer<WebElement>... active) {
		WebDriverWait wdw = new WebDriverWait(wd, waitTime);
		WebElement res = wdw.until(w -> w.findElement(By.cssSelector(cssQuery)));
		if (Objects.isNull(res)) {
			Stream.empty();
		}
		Arrays.asList(active).forEach(act -> act.accept(res));
		return Stream.of(res);
	}

	@SafeVarargs
	public static final Stream<WebElement> findByCss(SearchContext wd, String selector,
			Consumer<WebElement>... active) {
		try {
			WebElement element = wd.findElement(By.cssSelector(selector));
			for (Consumer<WebElement> cons : active) {
				cons.accept(element);
			}
			if (Objects.nonNull(element)) {
				Stream.of(element);
			}
		} catch (NoSuchElementException e) {
			Logs.info(e);
		}
		return Stream.empty();
	}

	@SafeVarargs
	public static final Stream<WebElement> findsByCss(SearchContext wd, String selector,
			Consumer<WebElement>... active) {
		try {
			List<WebElement> elements = wd.findElements(By.cssSelector(selector));
			for (WebElement element : elements) {
				for (Consumer<WebElement> cons : active) {
					cons.accept(element);
				}
			}
			return elements.stream();
		} catch (NoSuchElementException e) {
			Logs.info(e);
		}
		return Stream.empty();
	}

}
