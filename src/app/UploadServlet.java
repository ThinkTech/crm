package app;

import java.io.File;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

@SuppressWarnings("serial")
@WebServlet("/documents/upload.html")
public class UploadServlet extends HttpServlet {

	public void doPost(HttpServletRequest request,HttpServletResponse response) throws ServletException, IOException {
		String structure_id = request.getParameter("structure_id");
		String project_id = request.getParameter("project_id");
		String dir = "structure_"+structure_id+"/"+"project_"+project_id;
		boolean isMultipart = ServletFileUpload.isMultipartContent(request);
		if(isMultipart){
			try {
				ServletFileUpload upload = new ServletFileUpload();
				FileItemIterator iter = upload.getItemIterator(request);
				FileManager manager = new FileManager();
				while(iter.hasNext()) {
					FileItemStream item = iter.next(); 
					String name = new File(item.getName()).getName();
					manager.upload(dir+"/"+name,item.openStream());
				}
			}catch(Exception e){
			}
		}
		response.getWriter().write("{\"status\" : 1}");
	}
	
}