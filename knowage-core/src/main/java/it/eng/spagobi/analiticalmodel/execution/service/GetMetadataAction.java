/*
 * Knowage, Open Source Business Intelligence suite
 * Copyright (C) 2016 Engineering Ingegneria Informatica S.p.A.
 *
 * Knowage is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Knowage is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package it.eng.spagobi.analiticalmodel.execution.service;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.json.JSONArray;

import it.eng.spagobi.analiticalmodel.document.bo.BIObject;
import it.eng.spagobi.analiticalmodel.document.bo.DocumentMetadataProperty;
import it.eng.spagobi.analiticalmodel.document.bo.SubObject;
import it.eng.spagobi.commons.dao.DAOFactory;
import it.eng.spagobi.commons.serializer.SerializerFactory;
import it.eng.spagobi.commons.services.AbstractSpagoBIAction;
import it.eng.spagobi.commons.utilities.messages.MessageBuilder;
import it.eng.spagobi.tools.objmetadata.bo.ObjMetacontent;
import it.eng.spagobi.tools.objmetadata.bo.ObjMetadata;
import it.eng.spagobi.utilities.exceptions.SpagoBIServiceException;
import it.eng.spagobi.utilities.service.JSONSuccess;

/**
 *
 * @author Zerbetto Davide
 *
 */
public class GetMetadataAction extends AbstractSpagoBIAction {

	public static final String SERVICE_NAME = "GET_METADATA_ACTION";

	// REQUEST PARAMETERS
	public static final String OBJECT_ID = "OBJECT_ID";
	public static final String SUBOBJECT_ID = "SUBOBJECT_ID";

	//GENERAL METADATA NAMES
	public static final String LABEL = "metadata.docLabel";
	public static final String NAME = "metadata.docName";
	public static final String TYPE = "metadata.docType";
	public static final String ENG_NAME = "metadata.docEngine";
	public static final String RATING = "metadata.docRating";
	public static final String SUBOBJ_NAME = "metadata.subobjName";

	// logger component
	private static Logger logger = Logger.getLogger(GetMetadataAction.class);

	@Override
	public void doService() {
		logger.debug("IN");
		try {
			JSONArray toReturn = new JSONArray();

			Integer objectId = this.getAttributeAsInteger(OBJECT_ID);
			logger.debug("Object id = " + objectId);
			Integer subObjectId = null;
			try {
				subObjectId = this.getAttributeAsInteger(SUBOBJECT_ID);
			} catch (NumberFormatException e) {}
			logger.debug("Subobject id = " + subObjectId);

			List metaDataAndContents = new ArrayList();

			HttpServletRequest httpRequest = getHttpRequest();
			MessageBuilder msgBuild = new MessageBuilder();
			Locale locale = msgBuild.getLocale(httpRequest);

			//START GENERAL METADATA
			if(subObjectId!=null){
				//SubObj Name
				ObjMetadata metaSubObjName = new ObjMetadata();
				String textSubName = msgBuild.getMessage(SUBOBJ_NAME, locale);
				metaSubObjName.setName(textSubName);
				metaSubObjName.setDataTypeCode("GENERAL_META");
				ObjMetacontent metaContentSubObjName = new ObjMetacontent();
				SubObject subobj = DAOFactory.getSubObjectDAO().getSubObject(subObjectId);
				metaContentSubObjName.setContent(subobj.getName().getBytes(UTF_8));
				DocumentMetadataProperty metaAndContentSubObjName = new DocumentMetadataProperty();
				metaAndContentSubObjName.setMetadataPropertyDefinition(metaSubObjName);
				metaAndContentSubObjName.setMetadataPropertyValue(metaContentSubObjName);
				metaDataAndContents.add(metaAndContentSubObjName);
			}

			BIObject obj = DAOFactory.getBIObjectDAO().loadBIObjectById(objectId);
			//Obj Label
			ObjMetadata metaObjLabel = new ObjMetadata();
			String textLabel = msgBuild.getMessage(LABEL, locale);
			metaObjLabel.setName(textLabel);
			metaObjLabel.setDataTypeCode("GENERAL_META");
			ObjMetacontent metaContentObjLabel = new ObjMetacontent();
			metaContentObjLabel.setContent(obj.getLabel().getBytes(UTF_8));
			DocumentMetadataProperty metaAndContentObjLabel = new DocumentMetadataProperty();
			metaAndContentObjLabel.setMetadataPropertyDefinition(metaObjLabel);
			metaAndContentObjLabel.setMetadataPropertyValue(metaContentObjLabel);
			metaDataAndContents.add(metaAndContentObjLabel);

			//Obj Name
			ObjMetadata metaObjName = new ObjMetadata();
			String textName = msgBuild.getMessage(NAME, locale);
			metaObjName.setName(textName);
			metaObjName.setDataTypeCode("GENERAL_META");
			ObjMetacontent metaContentObjName = new ObjMetacontent();
			metaContentObjName.setContent(obj.getName().getBytes(UTF_8));
			DocumentMetadataProperty metaAndContentObjName = new DocumentMetadataProperty();
			metaAndContentObjName.setMetadataPropertyDefinition(metaObjName);
			metaAndContentObjName.setMetadataPropertyValue(metaContentObjName);
			metaDataAndContents.add(metaAndContentObjName);


			//Obj Type
			ObjMetadata metaObjType = new ObjMetadata();
			String textType = msgBuild.getMessage(TYPE, locale);
			metaObjType.setName(textType);
			metaObjType.setDataTypeCode("GENERAL_META");
			ObjMetacontent metaContentObjType = new ObjMetacontent();
			metaContentObjType.setContent(obj.getBiObjectTypeCode().getBytes(UTF_8));
			DocumentMetadataProperty metaAndContentObjType = new DocumentMetadataProperty();
			metaAndContentObjType.setMetadataPropertyDefinition(metaObjType);
			metaAndContentObjType.setMetadataPropertyValue(metaContentObjType);
			metaDataAndContents.add(metaAndContentObjType);

			/*
			//Obj Rating
			ObjMetadata metaObjRating = new ObjMetadata();
			String textRating = msgBuild.getMessage(RATING, locale);
			metaObjRating.setName(textRating);
			metaObjRating.setDataTypeCode("GENERAL_META");
			ObjMetacontent metaContentObjRating = new ObjMetacontent();
			Double temp =  DAOFactory.getBIObjectRatingDAO().calculateBIObjectRating(obj);
			String docRating = ( temp != null ? temp.toString() : "" );
			metaContentObjRating.setContent(docRating.getBytes(StandardCharsets.UTF_8));
			ObjMetaDataAndContent metaAndContentObjRating = new ObjMetaDataAndContent();
			metaAndContentObjRating.setMeta(metaObjRating);
			metaAndContentObjRating.setMetacontent(metaContentObjRating);
			metaDataAndContents.add(metaAndContentObjRating);*/

			//Obj Engine Name
			ObjMetadata metaObjEngineName = new ObjMetadata();
			String textEngName = msgBuild.getMessage(ENG_NAME, locale);
			metaObjEngineName.setName(textEngName);
			metaObjEngineName.setDataTypeCode("GENERAL_META");
			ObjMetacontent metaContentObjEngineName = new ObjMetacontent();
			metaContentObjEngineName.setContent(obj.getEngine().getName().getBytes(UTF_8));
			DocumentMetadataProperty metaAndContentObjEngineName = new DocumentMetadataProperty();
			metaAndContentObjEngineName.setMetadataPropertyDefinition(metaObjEngineName);
			metaAndContentObjEngineName.setMetadataPropertyValue(metaContentObjEngineName);
			metaDataAndContents.add(metaAndContentObjEngineName);

			//END GENERAL METADATA


			List metadata = DAOFactory.getObjMetadataDAO().loadAllObjMetadata();
			if (metadata != null && !metadata.isEmpty()) {
				Iterator it = metadata.iterator();
				while (it.hasNext()) {
					ObjMetadata objMetadata = (ObjMetadata) it.next();
					ObjMetacontent objMetacontent = DAOFactory.getObjMetacontentDAO().loadObjMetacontent(objMetadata.getObjMetaId(), objectId, subObjectId);
					DocumentMetadataProperty metaAndContent = new DocumentMetadataProperty();
					metaAndContent.setMetadataPropertyDefinition(objMetadata);
					metaAndContent.setMetadataPropertyValue(objMetacontent);
					metaDataAndContents.add(metaAndContent);
				}
			}

			toReturn = (JSONArray) SerializerFactory.getSerializer("application/json").serialize(metaDataAndContents, null);

			writeBackToClient( new JSONSuccess( toReturn ) );

		} catch (Exception e) {
			throw new SpagoBIServiceException(SERVICE_NAME, "Exception occurred while retrieving metadata", e);
		} finally {
			logger.debug("OUT");
		}
	}

}
