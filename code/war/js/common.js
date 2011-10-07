function getDisabledString() {
	return "<font color=\"red\"><textarea class = \"textvalue\" type=\"text\" rows=\"1\" cols=\"100\" name=\"" + 
	STUDENT_JUSTIFICATION + 0 + "\" id=\"" + STUDENT_JUSTIFICATION + 0 + "\" disabled=\"true\">" +
	"N.A.</textarea></font>";
}


function getEvalationOptionString() {
	return "<option value=\"200\">Equal share + 100%</option>" +
	"<option value=\"190\">Equal share + 90%</option>" +
	"<option value=\"180\">Equal share + 80%</option>" +
	"<option value=\"170\">Equal share + 70%</option>" +
	"<option value=\"160\">Equal share + 60%</option>" +
	"<option value=\"150\">Equal share + 50%</option>" +
	"<option value=\"140\">Equal share + 40%</option>" +
	"<option value=\"130\">Equal share + 30%</option>" +
	"<option value=\"120\">Equal share + 20%</option>" +
	"<option value=\"110\">Equal share + 10%</option>" +
	"<option value=\"100\" SELECTED>Equal Share</option>" +
	"<option value=\"90\">Equal share - 10%</option>" +
	"<option value=\"80\">Equal share - 20%</option>" +
	"<option value=\"70\">Equal share - 30%</option>" +
	"<option value=\"60\">Equal share - 40%</option>" +
	"<option value=\"50\">Equal share - 50%</option>" +
	"<option value=\"40\">Equal share - 60%</option>" +
	"<option value=\"30\">Equal share - 70%</option>" +
	"<option value=\"20\">Equal share - 80%</option>" +
	"<option value=\"10\">Equal share - 90%</option>" +
	"<option value=\"0\">0%</option>" +
	"<option value=\"-101\">Not Sure</option>";
}

function getTimeOptionString() {
	return "<option value=\"1\">0100H</option>" +
	"<option value=\"2\">0200H</option>" +
	"<option value=\"3\">0300H</option>" +
	"<option value=\"4\">0400H</option>" +
	"<option value=\"5\">0500H</option>" +
	"<option value=\"6\">0600H</option>" +
	"<option value=\"7\">0700H</option>" +
	"<option value=\"8\">0800H</option>" +
	"<option value=\"9\">0900H</option>" +
	"<option value=\"10\">1000H</option>" +
	"<option value=\"11\">1100H</option>" +
	"<option value=\"12\">1200H</option>" +
	"<option value=\"13\">1300H</option>" +
	"<option value=\"14\">1400H</option>" +
	"<option value=\"15\">1500H</option>" +
	"<option value=\"16\">1600H</option>" +
	"<option value=\"17\">1700H</option>" +
	"<option value=\"18\">1800H</option>" +
	"<option value=\"19\">1900H</option>" +
	"<option value=\"20\">2000H</option>" +
	"<option value=\"21\">2100H</option>" +
	"<option value=\"22\">2200H</option>" +
	"<option value=\"23\">2300H</option>" +
	"<option value=\"24\" SELECTED>2359H</option>";
}


function getTimezoneOptionString() {
	return "<option value=\"-12\">UTC -12:00</option>" +
	"<option value=\"-11\">UTC -11:00</option>" +
	"<option value=\"-10\">UTC -10:00</option>" +
	"<option value=\"-9\">UTC -09:00</option>" +
	"<option value=\"-8\">UTC -08:00</option>" +
	"<option value=\"-7\">UTC -07:00</option>" +
	"<option value=\"-6\">UTC -06:00</option>" +
	"<option value=\"-5\">UTC -05:00</option>" +
	"<option value=\"-4.5\">UTC -04:30</option>" +
	"<option value=\"-4\">UTC -04:00</option>" +
	"<option value=\"-3.5\">UTC -03:30</option>" +
	"<option value=\"-3\">UTC -03:00</option>" +
	"<option value=\"-2\">UTC -02:00</option>" +
	"<option value=\"-1\">UTC -01:00</option>" +
	"<option value=\"0\">UTC </option>" +
	"<option value=\"1\">UTC +01:00</option>" +
	"<option value=\"2\">UTC +02:00</option>" +
	"<option value=\"3\">UTC +03:00</option>" +
	"<option value=\"3.5\">UTC +03:30</option>" +
	"<option value=\"4\">UTC +04:00</option>" +
	"<option value=\"4.5\">UTC +04:30</option>" +
	"<option value=\"5\">UTC +05:00</option>" +
	"<option value=\"5.5\">UTC +05:30</option>" +
	"<option value=\"5.75\">UTC +05:45</option>" +
	"<option value=\"6\">UTC +06:00</option>" +
	"<option value=\"6.5\">UTC +06:30</option>" +
	"<option value=\"7\">UTC +07:00</option>" +
	"<option value=\"8\">UTC +08:00</option>" +
	"<option value=\"9\">UTC +09:00</option>" +
	"<option value=\"9.5\">UTC +09:30</option>" +
	"<option value=\"10\">UTC +10:00</option>" +
	"<option value=\"11\">UTC +11:00</option>" +
	"<option value=\"12\">UTC +12:00</option>" +
	"<option value=\"13\">UTC +13:00</option>";
}


function getGracePeriodOptionString() {
	return "<option value=\"5\">5 min</option>" +
	"<option value=\"10\">10 min</option>" +
	"<option value=\"15\">15 min</option>" +
	"<option value=\"20\">20 min</option>" +
	"<option value=\"25\">25 min</option>" +
	"<option value=\"30\">30 min</option>";
}