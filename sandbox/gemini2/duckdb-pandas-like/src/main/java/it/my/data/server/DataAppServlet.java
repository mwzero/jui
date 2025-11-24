package it.my.data.server;
import it.my.data.DuckDataFrame;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class DataAppServlet extends HttpServlet {
    private final DuckDataFrame df;
    private final String uploadDir;
    public DataAppServlet(DuckDataFrame df) {
        this.df = df;
        this.uploadDir = System.getProperty("java.io.tmpdir") + "/data_uploads";
        new File(uploadDir).mkdirs();
    }
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String sid = req.getSession(true).getId();
        if (ServletFileUpload.isMultipartContent(req)) {
            try {
                for(FileItem item : new ServletFileUpload(new DiskFileItemFactory()).parseRequest(req)) {
                    if(!item.isFormField() && "csvFile".equals(item.getFieldName())) {
                        File f = new File(uploadDir, sid + "_" + item.getName());
                        item.write(f);
                        df.registerCsv(f.getAbsolutePath(), "uploaded_data");
                        resp.getWriter().write("OK");
                        return;
                    }
                }
            } catch(Exception e) { resp.setStatus(500); resp.getWriter().write(e.getMessage()); }
        } else {
            try {
                resp.getWriter().write(df.query(req.getParameter("query")));
            } catch(Exception e) { resp.getWriter().write("Error: " + e.getMessage()); }
        }
    }
}