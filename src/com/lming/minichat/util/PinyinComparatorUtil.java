package com.lming.minichat.util;

import java.util.Comparator;

import com.lming.minichat.bean.GroupMemberBean;

import android.annotation.SuppressLint;

@SuppressLint("DefaultLocale")
public class PinyinComparatorUtil implements Comparator<GroupMemberBean> {

	public int compare(GroupMemberBean o1, GroupMemberBean o2) {
		if (o1 == null || o2 == null) {
			return -1;
		}
		if ("@".equals(o1.getSortLetters()) || "#".equals(o2.getSortLetters())) {
			return -1;
		} else if ("#".equals(o1.getSortLetters())
				|| "@".equals(o2.getSortLetters())) {
			return 1;
		} else {
			return o1.getSortPinYin().compareTo(o2.getSortPinYin());
		}
	}

	/**
	 * 更新sortModel根据 需要排序的字符拼音
	 * @param sortModel
	 * @param sortPinYin
	 * @return
	 */
	public GroupMemberBean getGroupMemberBeanBySort(GroupMemberBean sortModel,
			String sortPinYin) {
		// 汉字转换成拼音
		String sortString = sortPinYin.substring(0, 1).toUpperCase();
		// 正则表达式，判断首字母是否是英文字母
		if (sortString.matches("[A-Z]")) {
			sortModel.setSortLetters(sortString.toUpperCase());
			sortModel.setSortPinYin(sortPinYin.toUpperCase());
		} else {
			sortModel.setSortLetters("#");
			sortModel.setSortPinYin("#");
		}

		return sortModel;
	}

}
