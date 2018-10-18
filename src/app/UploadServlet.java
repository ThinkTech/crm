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
		if(ServletFileUpload.isMultipartContent(request)) {
			try {
				String folder = "structure_"+request.getParameter("structure_id")+"/"+"project_"+request.getParameter("project_id");
				FileManager manager = new FileManager(folder);
				FileItemIterator it = new ServletFileUpload().getItemIterator(request);
				while(it.hasNext()) {
					FileItemStream item = it.next(); 
					manager.upload(new File(item.getName()).getName(),item.openStream());
				}
			}catch(Exception e) {
			}
		}
		response.getWriter().write("{\"status\" : 1}");
	}
	
}