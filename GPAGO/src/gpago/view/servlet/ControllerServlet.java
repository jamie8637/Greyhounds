package gpago.view.servlet;

import gpago.model.entity.Greyhound;
import gpago.model.entity.Sponsor;
import gpago.view.GreyhoundFormBean;
import gpago.view.GreyhoundSaveResult;
import gpago.view.SponsorFormBean;
import gpago.view.ViewFacade;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Servlet implementation class ControllerServlet
 */
@WebServlet(urlPatterns = {"/admin/*", "/view-greyhounds/*", "/image"})
@MultipartConfig
public class ControllerServlet extends HttpServlet {
	private static final Logger logger = Logger.getLogger(ControllerServlet.class.getName());
	
	public static final String OPERAION_NEW_GREYHOUND = "new";
	public static final String OPERAION_EDIT_GREYHOUND = "edit";
	public static final String OPERAION_UNKNOWN = "UNKNOWN";

	public static final String ADDRESS_LIST_GREYHOUNDS_URI = "/greyhounds.jsp";
	public static final String ADDRESS_MANAGE_GREYHOUNDS_URI = "/WEB-INF/jsp/manageGreyhounds.jsp";
	public static final String ADDRESS_NEW_GREYHOUND_URI  = "/WEB-INF/jsp/editGreyhound.jsp";
	public static final String ADDRESS_EDIT_GREYHOUND_URI = "/WEB-INF/jsp/editGreyhound.jsp";
	

	public static final String ADDRESS_MANAGE_SPONSORS_URI = "/WEB-INF/jsp/manageSponsors.jsp";
	public static final String ADDRESS_NEW_SPONSOR_URI  = "/WEB-INF/jsp/editSponsor.jsp";
	public static final String ADDRESS_EDIT_SPONSOR_URI = "/WEB-INF/jsp/editSponsor.jsp";
	
	public static final String ADDRESS_NOT_FOUND = "/WEB-INF/jsp/notFound.jsp";
	public static final String ADDRESS_LOGIN_URI = "/admin/login";
	public static final String ADDRESS_GREYHOUND_ADMIN_PAGE_URI = "/admin/manage-greyhounds";
	public static final String ADDRESS_SPONSOR_ADMIN_PAGE_URI = "/admin/manage-sponsors";
	
	private static final long serialVersionUID = 1L;
	
	private ViewFacade viewFacade = new ViewFacade();

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public ControllerServlet() {
		super();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String uri = request.getRequestURI();
		logger.finest("URI: " + uri);
		
		// System.out.println("RequestURI: " + request.getContextPath());	
		// System.out.println("ContextPath: " + request.getContextPath());	
		
		uri = uri.toLowerCase();

		if (uri.endsWith("/logout")) { // The user is logging out.
			HttpSession session = request.getSession();
			if (session!=null)
				session.invalidate();
			request.setAttribute("facade", viewFacade); // We use the view facade to tailor what is exposed to jsp.
			response.sendRedirect(request.getContextPath() + ADDRESS_LOGIN_URI);
			
		} else if (uri.endsWith("/login")) {
			request.setAttribute("facade", viewFacade); // We use the view facade to tailor what is exposed to jsp.
			response.sendRedirect(request.getContextPath() + ADDRESS_GREYHOUND_ADMIN_PAGE_URI);
		} else if (uri.endsWith("/image")) { // It's a request for a greyhound image.
			Greyhound greyhound = viewFacade.getGreyhound(ParamUtils.getLongParameter(request, "id"));
			
			if ((greyhound!=null) && (greyhound.getImage1()!=null)) {
				
				int imageIndex = ParamUtils.getIntParameter(request, "idx");

				BufferedImage img = null;
				
				switch (imageIndex) {
				case 2:
					if (greyhound.getImage2()!=null)
						img = ImageIO.read(new ByteArrayInputStream(greyhound.getImage2()));
					break;
				case 3:
					if (greyhound.getImage3()!=null)
						img = ImageIO.read(new ByteArrayInputStream(greyhound.getImage3()));
					break;
				case 4:
					if (greyhound.getImage4()!=null)
						img = ImageIO.read(new ByteArrayInputStream(greyhound.getImage4()));
					break;
				case 5:
					if (greyhound.getImage5()!=null)
						img = ImageIO.read(new ByteArrayInputStream(greyhound.getImage5()));
					break;
				default:
					if (greyhound.getImage1()!=null)
						img = ImageIO.read(new ByteArrayInputStream(greyhound.getImage1()));
				}
				
				if (img!=null) {
					response.setContentType("image/jpeg");

					OutputStream out = response.getOutputStream();
					ImageIO.write(img, "jpg", out);
					out.close();
				}
				return;
			} else
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		} else if (uri.endsWith("/view-greyhounds")) { // Then user is viewing greyhounds available for adoption.
			request.setAttribute("facade", viewFacade); // We use the view facade to tailor what is exposed to jsp.
			forward(request, response, ADDRESS_LIST_GREYHOUNDS_URI);
		} else if (uri.endsWith("/admin/manage-greyhounds")) {
			request.setAttribute("facade", viewFacade); // We use the view facade to tailor what is exposed to jsp.
			forward(request, response, ADDRESS_MANAGE_GREYHOUNDS_URI);
		} else if (uri.endsWith("/admin/new-greyhound")) {
			GreyhoundFormBean bean = new GreyhoundFormBean(new Greyhound());
			request.setAttribute("greyhound", bean);
			forward(request, response, ADDRESS_NEW_GREYHOUND_URI);
		} else if (uri.endsWith("/admin/update-greyhound")) {
			Greyhound greyhound = viewFacade.getGreyhound(ParamUtils.getLongParameter(request, "id"));
			if (greyhound!=null) {
				GreyhoundFormBean bean = new GreyhoundFormBean(greyhound);
				request.setAttribute("greyhound", bean);
				forward(request, response, ADDRESS_EDIT_GREYHOUND_URI);
			} else {
				// A greyhound with the specified ID was not found or some error occurred, just display the manage greyhounds view.
				request.setAttribute("facade", viewFacade); // We use the view facade to tailor what is exposed to jsp.
				forward(request, response, ADDRESS_MANAGE_GREYHOUNDS_URI);
			}
		} else if (uri.endsWith("/admin/delete-greyhound")) {
			Greyhound greyhound = viewFacade.getGreyhound(ParamUtils.getLongParameter(request, "id"));
			if (greyhound!=null) {
				viewFacade.removeGreyhound(greyhound);				
			}
			request.setAttribute("facade", viewFacade); // We use the view facade to tailor what is exposed to jsp.
			response.sendRedirect(request.getContextPath() + ADDRESS_GREYHOUND_ADMIN_PAGE_URI);
		} else if (uri.endsWith("/admin/delete-sponsor")) {
			Sponsor sponsor = viewFacade.getSponsor(ParamUtils.getLongParameter(request, "id"));
			if (sponsor!=null) {
				viewFacade.removeSponsor(sponsor);
			}
			request.setAttribute("facade", viewFacade); // We use the view facade to tailor what is exposed to jsp.
			response.sendRedirect(request.getContextPath() + ADDRESS_SPONSOR_ADMIN_PAGE_URI);	
		} else if (uri.endsWith("/admin/manage-sponsors")) {
			request.setAttribute("facade", viewFacade); // We use the view facade to tailor what is exposed to jsp.
			forward(request, response, ADDRESS_MANAGE_SPONSORS_URI);
		} else if (uri.endsWith("/admin/new-sponsor")) {
			SponsorFormBean bean = new SponsorFormBean(request, new Sponsor());
			request.setAttribute("sponsor", bean);
			forward(request, response, ADDRESS_NEW_SPONSOR_URI);
		} else if (uri.endsWith("/admin/update-sponsor")) {
			Sponsor sponsor = viewFacade.getSponsor(ParamUtils.getLongParameter(request, "id"));

			if (sponsor!=null) {
				SponsorFormBean bean = new SponsorFormBean(request, sponsor);
				request.setAttribute("sponsor", bean);
				forward(request, response, ADDRESS_EDIT_SPONSOR_URI);
			} else {
				// A sponsor with the specified ID was not found or some error occurred, just display the manage sponsor view.
				request.setAttribute("facade", viewFacade); // We use the view facade to tailor what is exposed to jsp.
				forward(request, response, ADDRESS_MANAGE_SPONSORS_URI);
			}
		} else if (uri.endsWith("/admin")) {
			request.setAttribute("facade", viewFacade); // We use the view facade to tailor what is exposed to jsp.
			response.sendRedirect(request.getContextPath() + "/admin/manage-greyhounds");
		} else {
			forward(request, response, ADDRESS_NOT_FOUND);
		}
	}
	
	private void forward(HttpServletRequest request, HttpServletResponse response, String address) throws ServletException, IOException {
		RequestDispatcher dispatcher = request.getRequestDispatcher(address);
		dispatcher.forward(request, response);
	}
	
	/**
	 * doPost is called when we are saving an entity (e.g. Greyhound, Sponsor).
	 * 
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {		
		String uri = request.getRequestURI();
		logger.finest("URI: " + uri);
		
		uri = uri.toLowerCase();

		if (uri.endsWith("/save-greyhound")) { // Save the greyhound record.
			handleSaveGreyhound(request, response);
		} else if (uri.endsWith("/save-sponsor")) { // Save the sponsor record.
			handleSaveSponsor(request, response);
		}
	}
	
	private void handleSaveGreyhound(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		GreyhoundSaveResult saveResult = viewFacade.saveOrUpdateGreyhound(request);
		
		if (saveResult.isSuccessful()) {
			// We're done adding or updating record, go back to manage greyhounds page.
			request.setAttribute("facade", viewFacade); // We use the view facade to tailor what is exposed to jsp.
			response.sendRedirect(request.getContextPath() + "/admin/manage-greyhounds");
		} else {
			logger.finest("Invalid greyhound");
			
			request.setAttribute("greyhound", saveResult.getGreyhoundBean());
			forward(request, response, ADDRESS_EDIT_GREYHOUND_URI);
		}
	}
	
	private void handleSaveSponsor(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// If an id parameter exists, try to load a sponsor record for that id and initialize the sponsor object from the request parameters.
		Sponsor sponsor  = viewFacade.getSponsor(ParamUtils.getLongParameter(request, "id"));
		
		if (sponsor==null) {
			// No id was specified (or a sponsor with that id was not found in the database)
			// Therefore, we're creating new sponsor. Create a new sponsor object and initialize from parameters			
			sponsor = new Sponsor();
		}

		// Wrap the sponsor object in a SponsorFormBean to be used by the jsp's.
		SponsorFormBean bean = new SponsorFormBean(request, sponsor);
		
		if (bean.isValid()) {
			logger.finest("Saving " + sponsor);
			viewFacade.saveSponsor(sponsor);
	        
			// We're done adding or updating record, go back to manage sponsors page.
			request.setAttribute("facade", viewFacade); // We use the view facade to tailor what is exposed to jsp.
			response.sendRedirect(request.getContextPath() + "/admin/manage-sponsors");
		} else { // the sponsor is not valid.  Redisply the edit sponsor jsp.
			logger.finest("Invalid sponsor");
			
			request.setAttribute("sponsor", bean);
			forward(request, response, ADDRESS_EDIT_SPONSOR_URI);
		}
	}
}
