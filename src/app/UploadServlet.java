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
		boolean isMultipart = ServletFileUpload.isMultipartContent(request);
		if(isMultipart){
			try {
				String directory = "structure_"+request.getParameter("structure_id")+"/"+"project_"+request.getParameter("project_id");
				FileItemIterator iter = new ServletFileUpload().getItemIterator(request);
				FileManager manager = new FileManager();
				while(iter.hasNext()) {
					FileItemStream item = iter.next(); 
					manager.upload(directory+"/"+new File(item.getName()).getName(),item.openStream());
				}
			}catch(Exception e){
			}
		}
		response.getWriter().write("{\"status\" : 1}");
	}
	
}