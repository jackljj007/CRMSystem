package com.springside.webwork.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springside.modules.persistence.SearchFilter;
import org.springside.modules.utils.Collections3;

import com.google.common.collect.Lists;

import jxl.CellType;
import jxl.Workbook;
import jxl.write.DateTime;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

@Service
public class CommonService {
	
	public static <T> Specification<T> bySearchFilter(final Collection<SearchFilter> filters, final Class<T> entityClazz) {
		return new Specification<T>() {
			@Override
			public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
				if (Collections3.isNotEmpty(filters)) {

					List<Predicate> predicates = Lists.newArrayList();
					for (SearchFilter filter : filters) {
						// nested path translate, 如Task的名为"user.name"的filedName, 转换为Task.user.name属性
						String[] names = StringUtils.split(filter.fieldName, ".");
						Path<String> expression = root.get(names[0]);
						for (int i = 1; i < names.length; i++) {
							expression = expression.get(names[i]);
						}

						// logic operator
						switch (filter.operator) {
						case EQ:
							predicates.add(builder.equal(expression, filter.value));
							break;
						case LIKE:
							predicates.add(builder.like(expression, "%" + filter.value + "%"));
							break;
						case GT:
							predicates.add(builder.greaterThan(expression, (Comparable) filter.value));
							break;
						case LT:
							predicates.add(builder.lessThan(expression, (Comparable) filter.value));
							break;
						case GTE:
							predicates.add(builder.greaterThanOrEqualTo(expression, (Comparable) filter.value));
							break;
						case LTE:
							predicates.add(builder.lessThanOrEqualTo(expression, (Comparable) filter.value));
							break;
						}
					}

					// 将所有条件用 or 联合起来
					if (!predicates.isEmpty()) {
						return builder.or(predicates.toArray(new Predicate[predicates.size()]));
					}
				}

				return builder.conjunction();
			}
		};
	}
	
	/**
	 * 创建分页请求.
	 */
	public PageRequest buildPageRequest(int pageNumber, int pagzSize, Direction sortType) {
		Sort sort = new Sort(sortType, "id");
		return new PageRequest(pageNumber - 1, pagzSize, sort);
	}
	
	public void exportFile(HttpServletResponse response, File file, boolean isDel) throws IOException {
		OutputStream out = null;
		InputStream in = null;

		//获得文件名
		String filename = URLEncoder.encode(file.getName(), "UTF-8");
		//定义输出类型(下载)
		response.setContentType("application/force-download");
		response.setHeader("Location", filename);
		//定义输出文件头
		response.setHeader("Content-Disposition", "attachment;filename=" + filename);
		try {
			out = response.getOutputStream();
			in = new FileInputStream(file.getPath());

			byte[] buffer = new byte[1024];
			int len;
			//System.out.println("this is first time begin*****");
			while ((len = in.read(buffer)) > 0) {
				out.write(buffer, 0, len);
			}
			//System.out.println("this is first time end*****");
		} finally {
			if (in != null) {
				in.close();
			}
			if (out != null) {
				out.close();
			}
		}
		
		if (isDel) {
			//删除文件,删除前关闭所有的Stream.
//			file.delete();
			this.deleteFile(file);
		}
	}
	
	private void deleteFile(File file) {
		if (file.isDirectory()) {
			File[] files = file.listFiles();
			for (File f : files) {
				deleteFile(f);
			}
			file.delete();
		} else if (file.isFile()) {
			file.delete();
		}
	}
	
	/**
	 * 生成excel文件(文件标题栏与文件内容一定要对应)
	 * 只能生成一个sheet，且名字为sheet1
	 * @param os
	 * @param title (excel文件标题栏)
	 * @param lists (excel文件内容)
	 * @throws IOException
	 * @throws RowsExceededException
	 * @throws WriteException
	 */
	public void writeExcel(OutputStream os, String[] title, CellType[] types, List<Object[]> lists) throws IOException, RowsExceededException, WriteException {
		//创建可以写入的Excel工作薄(默认运行生成的文件在tomcat/bin下 )
		WritableWorkbook wwb = Workbook.createWorkbook(os);
		//生成工作表,(name:First Sheet,参数0表示这是第一页)
		WritableSheet sheet = wwb.createSheet("Sheet1", 0);

		//开始写入第一行(即标题栏)
		if(title.length == 0 || title != null) {
			for (int i = 0; i < title.length; i++) {
				//用于写入文本内容到工作表中去
				Label label = null;
				//在Label对象的构造中指明单元格位置(参数依次代表列数、行数、内容 )
				label = new Label(i, 0, title[i]);
				//将定义好的单元格添加到工作表中
				sheet.addCell(label);
			}
		}

		//开始写入内容
		for (int i = 0; i < lists.size(); i++) {
			//获取一条(一行)记录
			Object[] cells = lists.get(i);
			for (int j = 0; j < cells.length; j++) {
				//类型判断
				if (types[j] == CellType.DATE) {
					if (cells[j] instanceof Long) {
						Calendar c = Calendar.getInstance();
						c.setTimeInMillis((Long) cells[j]);
						//获取时间数据由于时区相差8小时 获取系统时间 相加 补差8小时
						c.add(Calendar.MILLISECOND, c.getTimeZone().getRawOffset());
						//生成一个保存日期的单元格,必须使用DateTime的完整包路径,否则有语法歧义,值为new Date()
						DateTime date = new DateTime(j, i + 1, c.getTime(), DateTime.GMT);
						sheet.addCell(date);
//					} else if (cells[j] instanceof Date) {
//					} else if (cells[j] instanceof Calendar) {
					}

				} else if (types[j] == CellType.LABEL) {
					Label label = null;
					label = new Label(j, i + 1, (String) cells[j]);
					sheet.addCell(label);
				} else if (types[j] == CellType.NUMBER) {
					if (cells[j].getClass().equals(Integer.class)) {	// int类型
						jxl.write.Number number = new jxl.write.Number(j, i + 1, (Integer) cells[j]);
						sheet.addCell(number);
					} else if (cells[j].getClass().equals(Double.class)) {
						jxl.write.Number number = new jxl.write.Number(j, i + 1, (Double) cells[j]);
						sheet.addCell(number);
					} else if (cells[j].getClass().equals(Float.class)) {
						jxl.write.Number number = new jxl.write.Number(j, i + 1, (Float) cells[j]);
						sheet.addCell(number);
					}
				}
			}
		}

		/*
		 * 生成一个保存数字的单元格,必须使用Number的完整包路径,否则有语法歧义,值为789.123 jxl.write.Number
		 * number = new jxl.write.Number(col, row, 555.12541);
		 * sheet.addCell(number);
		 */

		//写入数据
		wwb.write();
		//关闭文件
		wwb.close();
		//关闭输出流
		os.close();
	}
	
	/**
	 * string转换数字类型
	 * @param obj
	 * @return
	 */
	public String stringParse(Object obj) {
		String str = (String) obj;
		str = this.isEmpty(str) ? "0" : str;
		return str;
	}
	
	/**
	 * 是否为空<br>
	 * 包括Null和""
	 * @param str
	 * @return
	 */
	public boolean isEmpty(String str) {
		if(null == str || "".equals(str))
			return true;
		return false;
	}

	/**
	 * 去掉前后无意义的字符<br>
	 * 空格等
	 * @param str
	 * @return
	 */
	public String trim(String str) {
		if(str == null)
			return null;
		return str.trim();
	}
	
	public String dealNullStr(String str) {
		String ret = "";
		ret = str == null ? "" : str;
		return ret;
	}
	
	/**
	 * 创建文件或文件夹
	 * @param fileFolder
	 * @param fileName
	 * @return
	 */
	public File fileCreate(String fileFolder, String fileName) {
		File folder = new File(fileFolder);
		File file = new File(fileFolder + "/" + fileName);
		//如果文件夹不存在，则创建文件夹
		if (folder.exists() == false) {
			folder.mkdirs();	//多级目录
//			foder.mkdir();	//只创建一级目录
		}
		//如果文件不存在，则创建文件
		if (file.exists() == false) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return file;
	}

}
