package controllers.likecount;

import java.io.IOException;

import javax.persistence.EntityManager;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import models.Report;
import utils.DBUtil;

/**
 * Servlet implementation class LikeCountServlet
 */
@WebServlet("/reports/likecount")
public class LikeCountServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public LikeCountServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // TODO Auto-generated method stub

        EntityManager em = DBUtil.createEntityManager();

        Report r = em.find(Report.class, Integer.parseInt(request.getParameter("id")));


        int like_count = Integer.parseInt(request.getParameter("like_count"));
        String name =  (String)request.getParameter("report_name");
        String tittle =  (String)request.getParameter("report_tittle");

        like_count += 1;

        r.setLike_count(like_count);

        em.getTransaction().begin();
        em.getTransaction().commit();
        em.close();

        request.getSession().removeAttribute("like_count");
        request.getSession().setAttribute("like_count", like_count);

        request.getSession().setAttribute("flush", name +"さんの日報『"+ tittle +"』にいいねをしました。");

        response.sendRedirect(request.getContextPath() + "/reports/index");

    }

}
