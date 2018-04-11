package org.yansou.common.time;

import java.util.ArrayList;
import java.util.function.Consumer;
import java.util.stream.LongStream;

import org.apache.commons.lang3.StringUtils;

/**
 * 时间差计时器。 <BR>
 * 创建时记录一次时间，其后每次调用burie()都增加一次计数。
 * 
 * @author Administrator
 *
 */
public class TimeStat {
	final private ArrayList<Long> timeList = new ArrayList<>();

	public TimeStat() {
		burie();
	}

	/**
	 * 记录一个时间
	 */
	public void burie() {
		timeList.add(System.currentTimeMillis());
	}
	public static TimeStat create(){
		return new TimeStat();
	}
	/**
	 * 
	 * 
	 * @param message
	 *            字符串中的{}会被替换成时间。
	 * @param consumer
	 *            打印到某个函数。
	 */
	public void buriePrint(String message, Consumer<String> consumer) {
		burie();
		long[] times = getDiff();
		int index = times.length;
		for (;;) {
			index--;
			if (!StringUtils.contains(message, "{}") || index < 0) {
				break;
			}
			String timeStr = String.valueOf(times[index]);
			message = message.replaceFirst("\\{\\}(?=[^\\{\\}]*?$)", timeStr);
		}
		consumer.accept(message);
	}

	/**
	 * 
	 * @param message
	 *            字符串中的{}会被替换成时间。并打印。
	 */
	public void buriePrint(String message) {
		this.buriePrint(message, System.out::println);
	}

	public long[] getDiff() {
		long[] res = new long[timeList.size() - 1];
		for (int i = 0; i < timeList.size() - 1; i++) {
			res[i] = timeList.get(i + 1) - timeList.get(i);
		}
		return res;
	}

	public LongStream getDiffStream() {
		return LongStream.of(getDiff());
	}

}
