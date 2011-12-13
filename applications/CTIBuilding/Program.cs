using System;
using System.Collections.Generic;
using System.Text;
using System.IO;
using System.Net;
using System.Data.OleDb;
using System.Data;
using System.Security.Permissions;
using Microsoft.Win32;


namespace CTIBuilding
{
    class Program
    {
        static String pathToData = "c:\\alarms2.mdb";
        static int time0 = 1251905971;

        /// <summary>
        /// The main entry point for the application.
        /// </summary>
        [STAThread]
        static void Main()
        {
            string aConnectionString = null;
            OleDbConnection aConnection;
            OleDbDataAdapter oledbAdapter;
            DataSet ds = new DataSet();
            string sql = null;

            // Registry key path
            const string userRoot = "HKEY_CURRENT_USER";
            const string subkey = "CTIBuilding";
            const string keyName = userRoot + "\\" + subkey;

            RegistryKey rk = Registry.CurrentUser.OpenSubKey(subkey);
            if (rk == null)
            {
                RegistryKey key = Microsoft.Win32.Registry.CurrentUser.CreateSubKey(subkey);
                key.SetValue("time0", 1, RegistryValueKind.DWord);
            }

            int tInteger = (int)Registry.GetValue(keyName, "time0", -1);
            Console.WriteLine("(Default): {0}", tInteger);


            // OLEDB - retrieve alarms
            aConnectionString = "Provider=Microsoft.Jet.OLEDB.4.0;Data Source=" + pathToData;
            sql = "select ALARM_TIME, CONTROLLER_NAME, USER_ADDRESS, VALUE from Alarms where ALARM_TIME>" + tInteger.ToString() + " order by ALARM_TIME ASC";
            aConnection = new OleDbConnection(aConnectionString);

            try
            {
                HttpWebRequest request = null;
                aConnection.Open();
                oledbAdapter = new OleDbDataAdapter(sql, aConnection);
                oledbAdapter.Fill(ds, "OLEDB Temp Table");
                oledbAdapter.Dispose();
                aConnection.Close();
                int i = 0;
                DataTable dt = ds.Tables[0];
                foreach (DataRow dr in dt.Rows)
                {
                    string str = dr[3].ToString().Trim();
                    Console.WriteLine("---------------");
                    Console.WriteLine("    " +i+ "    ");
                    Console.WriteLine(dr[0].ToString());
                    Console.WriteLine(dr[1].ToString());
                    Console.WriteLine(dr[2].ToString());
                    Console.WriteLine(dr[3].ToString());
                    i++;

                    // check value from OLEDB if double or string
                    double Num;
                    bool isNum = double.TryParse(str, out Num);

                    if (isNum)
                    {
                        // Send Data to Uberdust if int
                        request = (HttpWebRequest)WebRequest.Create("http://uberdust.cti.gr/rest/testbed/4/node/urn:ctibuilding:" + dr[1].ToString() + "/capability/urn:ctibuilding:" + dr[2].ToString() + "/insert/timestamp/" + dr[0].ToString() + "000" + "/reading/" + Num + "/");
                        using (HttpWebResponse response = (HttpWebResponse)request.GetResponse())
                        {
                            //ignore 
                        }
                    }
                    else
                    {
                        // Send Data to Uberdust if string
                        request = (HttpWebRequest)WebRequest.Create("http://uberdust.cti.gr/rest/testbed/4/node/urn:ctibuilding:" + dr[1].ToString() + "/capability/urn:ctibuilding:" + dr[2].ToString() + "/insert/timestamp/" + dr[0].ToString() + "000" + "/stringreading/" + Num.ToString() + "/");
                        using (HttpWebResponse response = (HttpWebResponse)request.GetResponse())
                        {
                            //ignore 
                        }
                    }

                    time0 = Convert.ToInt32(dr[0].ToString());

                    //Set last value to Registry
                    Registry.SetValue(keyName, "time0", time0);

                }
                Console.WriteLine("---------------");
                Console.WriteLine("number of Row(s) - " + ds.Tables[0].Rows.Count);

            }
            //exception handling
            catch (OleDbException e)
            {
                Console.WriteLine("Error: {0}", e.Errors[0].Message);
            }
        }
    }
}
