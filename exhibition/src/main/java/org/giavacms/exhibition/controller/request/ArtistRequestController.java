package org.giavacms.exhibition.controller.request;

import java.io.Serializable;
import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.giavacms.common.annotation.HttpParam;
import org.giavacms.common.annotation.OwnRepository;
import org.giavacms.common.controller.AbstractRequestController;
import org.giavacms.common.model.Search;
import org.giavacms.exhibition.model.Artist;
import org.giavacms.exhibition.model.Participant;
import org.giavacms.exhibition.repository.ArtistRepository;
import org.giavacms.exhibition.service.ParticipantService;

@Named
@RequestScoped
public class ArtistRequestController extends AbstractRequestController<Artist>
         implements Serializable
{

   private static final long serialVersionUID = 1L;

   public static final String SEARCH = "q";
   public static final String ID_PARAM = "artist";
   public static final String EXHIBITION = "exhibition";
   public static final String CURRENT_PAGE_PARAM = "start";

   @Inject
   @OwnRepository(ArtistRepository.class)
   ArtistRepository artistRepository;

   @Inject
   ParticipantService participantService;

   @Inject
   @HttpParam(SEARCH)
   String search;

   @Inject
   @HttpParam(ID_PARAM)
   String id;

   @Inject
   @HttpParam(CURRENT_PAGE_PARAM)
   String start;

   @Inject
   @HttpParam(EXHIBITION)
   String exhibition;

   public ArtistRequestController()
   {
      super();
   }
   
   @Override
   protected void initSearch()
   {
      super.getSearch().getObj().setTitle(search);
      super.getSearch().getObj().getFaqCategory().setId(category);
      super.initSearch();
   }

   @Override
   public List<Artist> loadPage(int startRow, int pageSize)
   {
      Search<Participant> r = new Search<Participant>(Participant.class);
      r.getObj().getSubject().setType(Artist.TYPE);
      r.getObj().getExhibition().setId(exhibition);
      r.getObj().getSubject().setSurname(search);
      return (List<Artist>) participantService.getList(r, startRow, pageSize);
   }

   @Override
   public int totalSize()
   {
      // siamo all'interno della stessa richiesta per servire la quale è
      // avvenuta la postconstruct
      Search<Participant> r = new Search<Participant>(Participant.class);
      r.getObj().getSubject().setType(Artist.TYPE);
      r.getObj().getExhibition().setId(exhibition);
      r.getObj().getSubject().setSurname(search);
      return participantService.getListSize(r);
   }

   @Override
   public String getIdParam()
   {
      return ID_PARAM;
   }

   @Override
   public String getCurrentPageParam()
   {
      return CURRENT_PAGE_PARAM;
   }

   public boolean isScheda()
   {
      return getElement() != null && getElement().getId() != null;
   }

}
