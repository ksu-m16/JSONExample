package jexample;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringBufferInputStream;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import jexample.Record.OperationType;
import jexample.Record.PaymentType;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

//GSON library can be downloaded from: http://code.google.com/p/google-gson
//file: http://code.google.com/p/google-gson/downloads/detail?name=google-gson-2.2.2-release.zip

/* 
 * Typical java library often consists of three parts:
 * library code, documentation and source, 
 * They are often named: libname.jar, libname-doc.jar/zip, libname-src.jar/zip respectively.
 * 
 * Library is installed into project in following way:
 * library jar files are put to lib subfolder
 * library doc files into lib/doc
 * library src files info lib/src
 * 
 * Actions taken in Eclipse IDE:
 * 
 * Adding library:
 * - open project properties (right-click menu on project item)
 * - select Java Build Path -> Libraries -> Add JARs...
 * - select library.jar within project tree in workspace
 * 
 * Adding sources:
 * - library tree item appears in libaray list, expand it.
 * - select Source attachment item, click edit... button
 * - click Workspace, 
 * - select appropriate file under project/lib/src path;
 * - confirm selection.
 * 
 * Adding javadoc:
 * - select Javadoc location item, click edit... button.
 * - select Javadoc in archive option, then workspace file option;
 * - browse for appropriate doc file under project/lib/doc path;
 * - confirm selection;
 * 
 * Congratulations, now library is fully attached to project)))
 */


/*
“« ведь не должно касатьс€ архитектуры? “огда:
“ребуетс€ приложение с графическим интерфейсом дл€ контрол€ оборота
денежных средств. ƒолжны быть реализованы различные варианты
транзакций: 
пополнение: наличными и банковской карты, в долг;
расход: наличными и с карты;
перевод денежных средств внутри общего счета между "наличными",
"картой" и "заначкой";
ƒл€ каждой транзакции должна быть возможность выбрать примечание
(reason) из списка наиболее часто употребл€емых (например, еда, пиво, транспорт), или 
ввести примечание вручную.

ѕриложение должно подсчитывать текущий баланс (суммарный и по категори€м),
считать расходы за мес€ц. ћожно еще считать отдельно расходы за мес€ц
по категори€м.

валидатор - ок, попробуем)

задачки - это тоже интересно 
*/


class Record {		
	enum OperationType {
		INCOME, EXPENSE, TRANSFER, UNDEFINED
	}

	enum PaymentType {
		CASH, CARD, LOAN, UNDEFINED
	}	
	
	public OperationType operationType = OperationType.UNDEFINED;
	public PaymentType paymentType = PaymentType.UNDEFINED;
	public double amount = 0;
	String description = "undefined";
	public long timestamp = 0;
	
	Record(long timestamp, OperationType opType, PaymentType payType, double amount, String desc) {
		this.timestamp = timestamp;
		this.operationType = opType;
		this.paymentType = payType;
		this.amount = amount;
		this.description = desc;
	}
	
	@Override
	public String toString() {	
		return "ts: " + timestamp 
			+ ", ot: " + this.operationType 
			+ ", pt: " + this.paymentType 
			+ ", amt: " + amount 
			+ ", desc: '" + description + "'"; 
	}
}

class Journal {
	public List<Record> records = new LinkedList<Record>();
	
	public void serialize(OutputStream out) throws IOException {
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		OutputStreamWriter osw = new OutputStreamWriter(out);
		gson.toJson(records.toArray(), osw);
		osw.flush();		
	}
	
	public void deserialize(InputStream in) {
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		InputStreamReader isr = new InputStreamReader(in);			
		records = new LinkedList<Record>(Arrays.asList(gson.fromJson(isr, Record[].class)));		
	}
}

//Here for example byte array streams are used,
//in convinient code FileInputStream, FileOutputStream should be used instead

public class Main {
	public static void main(String args[]) {
		Journal j = new Journal();
		
		//DateFormat format = DateFormat.
		j.records.add(new Record(
			1232132132,
			OperationType.INCOME, 
			PaymentType.CARD, 
			100500, 
			"I've got a money"));
		
		j.records.add(new Record(
			321321,
			OperationType.EXPENSE, 
			PaymentType.LOAN, 
			100000, 
			"I owe nothing"));			
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();		
		try {
			j.serialize(baos);
		} catch (IOException e) {
			e.printStackTrace();
		}
		String data = new String(baos.toByteArray());
		
		System.out.println("Source data:");
		System.out.println("----------------");
		for (Record r : j.records) {
			System.out.println(r.toString());
		}
		System.out.println("----------------");
		System.out.println("");
		
		System.out.println("Serialized data:");
		System.out.println("----------------");
		System.out.println(data);
		System.out.println("----------------");
		System.out.println("");
		
		Journal jnew = new Journal();
		jnew.deserialize(new ByteArrayInputStream(data.getBytes()));
		
		System.out.println("Derialized data:");
		System.out.println("----------------");
		for (Record r : jnew.records) {
			System.out.println(r.toString());
		}
		System.out.println("----------------");
	}
}
