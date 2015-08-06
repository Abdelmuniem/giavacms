package org.giavacms.chalet.service.rs;

import org.apache.commons.io.IOUtils;
import org.giavacms.api.model.Search;
import org.giavacms.api.service.RsRepositoryService;
import org.giavacms.base.util.FileUtils;
import org.giavacms.base.util.HttpUtils;
import org.giavacms.base.util.ResourceUtils;
import org.giavacms.chalet.management.AppConstants;
import org.giavacms.chalet.model.Chalet;
import org.giavacms.chalet.model.Photo;
import org.giavacms.chalet.repository.ChaletRepository;
import org.giavacms.chalet.repository.PhotoRepository;
import org.giavacms.chalet.utils.PhotoUtils;
import org.giavacms.commons.jwt.annotation.AccountTokenVerification;
import org.giavacms.contest.model.Account;
import org.giavacms.contest.repository.AccountRepository;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;

import javax.annotation.Resource;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.NoResultException;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import javax.ws.rs.core.Response.Status;
import java.io.InputStream;
import java.security.Principal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Path(AppConstants.BASE_PATH + AppConstants.PHOTO_PATH)
@Stateless
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class PhotoRepositoryRs extends RsRepositoryService<Photo>
{

   private static final long serialVersionUID = 471883957307995404L;

   @Inject
   ChaletRepository chaletRepository;

   @Inject
   AccountRepository accountRepository;

   @Resource
   SessionContext sessionContext;

   public PhotoRepositoryRs()
   {
   }

   @Inject
   public PhotoRepositoryRs(PhotoRepository repository)
   {
      super(repository);
   }

   @POST
   @Path("/chalet/{chaletId}")
   @Consumes(MediaType.MULTIPART_FORM_DATA)
   @AccountTokenVerification
   // TODO _ RESIZE
   public Response addImage(@PathParam("chaletId") String chaletId,
            MultipartFormDataInput input)
            throws Exception
   {

      Chalet chalet = chaletRepository.find(chaletId);
      if (chalet == null)
      {
         return RsRepositoryService
                  .jsonResponse(Response.Status.INTERNAL_SERVER_ERROR, AppConstants.RS_MSG, AppConstants.ER7);
      }
      Principal callerPrincipal = sessionContext.getCallerPrincipal();
      String phone = (String) callerPrincipal.getName();
      logger.info("chaletId:" + chaletId + " - phone: " + phone);
      Account account = accountRepository.exist(phone);
      if (account == null)
      {
         return RsRepositoryService
                  .jsonResponse(Response.Status.INTERNAL_SERVER_ERROR, AppConstants.RS_MSG, AppConstants.ER8);
      }
      try
      {
         Map<String, List<InputPart>> formParts = input.getFormDataMap();
         List<InputPart> fileParts = formParts.get("file");
         for (InputPart filePart : fileParts)
         {
            try
            {
               Photo photo = saveImage(chaletId, phone, input, filePart);
               String output = "File saved to server location : " + photo.getName();
               logger.debug(output);
               return Response.status(Response.Status.OK).entity(photo).build();
            }
            catch (Exception e)
            {
               return RsRepositoryService.jsonResponse(Response.Status.INTERNAL_SERVER_ERROR, AppConstants.RS_MSG,
                        "Error writing file for accountId: " + phone + " - chaletId: " + chaletId);
            }
         }
         return RsRepositoryService.jsonResponse(Response.Status.BAD_REQUEST, AppConstants.RS_MSG,
                  "File part not found in form data");
      }
      catch (Exception e)
      {
         logger.error(e.getMessage(), e);
         return RsRepositoryService.jsonResponse(Response.Status.INTERNAL_SERVER_ERROR, AppConstants.RS_MSG,
                  "Error creating doc");
      }
   }

   @Override
   protected void preDelete(Object key) throws Exception
   {
      // DEVE ESISTERE LA FOTO
      Photo photo = getRepository().find(key);
      if (photo == null)
      {
         throw new Exception(AppConstants.ER9);
      }
      // DEVE ESISTERE ACCOUNT
      if (!sessionContext.isCallerInRole("ADMIN") || !sessionContext.isCallerInRole("SUPERVISOR"))
      {
         String phone = (String) sessionContext.getCallerPrincipal().getName();
         Account account = accountRepository.exist(phone);
         if (account == null)
         {
            throw new Exception(AppConstants.ER8);
         }
         // LA FOTO DEVE ESSERE DELL'ACCOUNT
         if (!photo.getAccountId().equals(phone))
         {
            throw new Exception(AppConstants.ER10);
         }
      }
   }

   @DELETE
   @Path("/{id}")
   @AccountTokenVerification
   public Response delete(@PathParam("id") String id) throws Exception
   {
      logger.info("@DELETE:" + id);
      try
      {
         preDelete(id);
      }
      catch (Exception e)
      {
         return jsonResponse(Response.Status.BAD_REQUEST, AppConstants.RS_MSG,
                  "Errore before deleting resource: " + id);
      }
      try
      {
         getRepository().delete(getRepository().castId(id));
         return jsonResponse(Response.Status.NO_CONTENT, AppConstants.RS_MSG, "Resource deleted for ID: " + id);
      }
      catch (NoResultException e)
      {
         logger.error(e.getMessage());
         return jsonResponse(Response.Status.NOT_FOUND, AppConstants.RS_MSG, "Resource not found for ID: " + id);
      }
      catch (Exception e)
      {
         logger.error(e.getMessage(), e);
         return jsonResponse(Response.Status.INTERNAL_SERVER_ERROR, AppConstants.RS_MSG,
                  "Error deleting resource for ID: " + id);
      }
   }

   private Photo saveImage(String chaletId, String accountId, MultipartFormDataInput input, InputPart filePart)
            throws Exception
   {
      Photo photo = new Photo();
      photo.setChaletId(chaletId);
      photo.setAccountId(accountId);
      photo.setCreated(new Date());
      photo.setActive(true);
      String uuid = UUID.randomUUID().toString();
      photo.setUuid(uuid);

      // Retrieve headers, read the Content-Disposition header to obtain the original name of the file
      MultivaluedMap<String, String> headers = filePart.getHeaders();
      String fileName = FileUtils.getLastPartOf(HttpUtils.parseFileName(headers));
      // Handle the body of that part with an InputStream
      InputStream istream = filePart.getBody(InputStream.class, null);
      byte[] byteArray = IOUtils.toByteArray(istream);
      String photoName = uuid + "." + FileUtils.getExtension(fileName);
      photo.setName(photoName);

      String absoluteFilename = ResourceUtils
               .createFile_(AppConstants.PHOTO_FOLDER, photoName, byteArray);
      logger.info("img create: " + absoluteFilename);
      photo = getRepository().persist(photo);
      return photo;
   }

   @PUT
   @Path("/{uuid}/approved")
   @AccountTokenVerification
   public Response approve(@PathParam("uuid") String id) throws Exception
   {
      try
      {
         if (!sessionContext.isCallerInRole(AppConstants.ROLE_ADMIN) || !sessionContext.isCallerInRole("SUPERVISOR"))
         {
            return RsRepositoryService
                     .jsonResponse(Response.Status.FORBIDDEN, AppConstants.RS_MSG, AppConstants.ER11);
         }

         // DEVE ESISTERE LA FOTO
         Photo photo = getRepository().find(id);
         if (photo == null)
         {
            throw new Exception(AppConstants.ER9);
         }
         photo.setApproved(true);
         photo.setApprovedDate(new Date());
         getRepository().update(photo);
         return Response.status(Response.Status.OK).entity(photo).build();
      }
      catch (Exception e)
      {
         logger.error(e.getMessage(), e);
         return jsonResponse(Response.Status.INTERNAL_SERVER_ERROR, AppConstants.RS_MSG,
                  "Error deleting resource for ID: " + id);
      }
   }

   @PUT
   @Path("/{uuid}/unapproved")
   @AccountTokenVerification
   public Response unapprove(@PathParam("uuid") String id) throws Exception
   {
      try
      {
         if (!sessionContext.isCallerInRole("ADMIN") || !sessionContext.isCallerInRole("SUPERVISOR"))
         {
            return RsRepositoryService
                     .jsonResponse(Response.Status.FORBIDDEN, AppConstants.RS_MSG, AppConstants.ER11);
         }

         // DEVE ESISTERE LA FOTO
         Photo photo = getRepository().find(id);
         if (photo == null)
         {
            throw new Exception(AppConstants.ER9);
         }
         photo.setApproved(false);
         photo.setApprovedDate(new Date());
         getRepository().update(photo);
         return Response.status(Response.Status.OK).entity(photo).build();
      }
      catch (Exception e)
      {
         logger.error(e.getMessage(), e);
         return jsonResponse(Response.Status.INTERNAL_SERVER_ERROR, AppConstants.RS_MSG,
                  "Error deleting resource for ID: " + id);
      }
   }

   @GET
   @Path("/chalets/all")
   @AccountTokenVerification
   public Response getChaletWithPhotos(@QueryParam("accountId") String accountId)
   {
      logger.info("@GET list");
      try
      {
         if (sessionContext.isCallerInRole(AppConstants.ROLE_ADMIN)
                  || sessionContext.isCallerInRole(AppConstants.ROLE_SUPERVISOR))
         {
            // gli admin non filtrano i propri
            accountId = null;
         }
         else
         {
            // gli altri vedono solo i propri
            Principal callerPrincipal = sessionContext.getCallerPrincipal();
            String phone = (String) callerPrincipal.getName();
            if (accountId == null || accountId.trim().isEmpty() || !phone.equals(accountId.trim()))
            {
               return RsRepositoryService
                        .jsonResponse(Response.Status.FORBIDDEN, AppConstants.RS_MSG, AppConstants.ER11);
            }
         }
         String chaletId = null;
         Boolean approved = null;
         Boolean evaluated = null;
         Search<Photo> search = PhotoUtils.makeSearch(chaletId, accountId, approved, evaluated);
         List<Chalet> list = ((PhotoRepository) getRepository()).withPhoto(search);
         // PaginatedListWrapper<T> wrapper = new PaginatedListWrapper<>();
         // wrapper.setList(list);
         // wrapper.setListSize(listSize);
         // wrapper.setStartRow(startRow);
         return Response.status(Response.Status.OK).entity(list)
                  .header("Access-Control-Expose-Headers", "startRow, pageSize, listSize")
                  .header("startRow", 0)
                  .header("pageSize", list.size())
                  .header("listSize", list.size())
                  .build();
      }
      catch (Exception e)
      {
         logger.error(e.getMessage(), e);
         return jsonResponse(Response.Status.INTERNAL_SERVER_ERROR, AppConstants.RS_MSG, "Error reading chalet list");
      }
   }

   @Override
   @GET
   @AccountTokenVerification
   public Response getList(
            @DefaultValue("0") @QueryParam("startRow") Integer startRow,
            @DefaultValue("10") @QueryParam("pageSize") Integer pageSize,
            @QueryParam("orderBy") String orderBy, @Context UriInfo ui)
   {
      logger.info("@GET list");
      try
      {
         Search<Photo> search = getSearch(ui, orderBy);
         if (!sessionContext.isCallerInRole(AppConstants.ROLE_ADMIN)
                  && !sessionContext.isCallerInRole(AppConstants.ROLE_SUPERVISOR))
         {
            search.getObj().setApproved(true);
         }
         int listSize = getRepository().getListSize(search);
         List<Photo> list = getRepository().getList(search, startRow, pageSize);
         return Response.status(Status.OK).entity(list)
                  .header("Access-Control-Expose-Headers", "startRow, pageSize, listSize")
                  .header("startRow", startRow)
                  .header("pageSize", pageSize)
                  .header("listSize", listSize)
                  .build();
      }
      catch (Exception e)
      {
         logger.error(e.getMessage(), e);
         return jsonResponse(Status.INTERNAL_SERVER_ERROR, "msg", "Error reading resource list");
      }
   }
}