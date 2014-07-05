package dinamica.security;

import java.util.UUID;
import dinamica.*;

/**
 * Generar una contrase�a random, se usa para modulos de "Olvid� mi contrase�a",
 * que deben regenerar una contrase�a temporal y enviarla por email. Esta clase
 * depende de que el Action tenga un validator con estos elementos como m�nimo:<br>
 * userlogin, passwd, passwd_clear. El campo passwd ser� la contrase�a regenerada y
 * encriptada como un HASH la cual ser� actualizada en BD, el campo passwd_clear ser�
 * la representaci�n en texto libre, para ser enviada por email. Se supone que el Action
 * que usa esta clase tiene lo necesario para validar captcha, el SQL para actualizar
 * la contrase�a en BD, etc.<br> El validator DEBE contener esos campos, pero el request
 * solo pasa el campo userlogin, ya que las contrase�as son computadas por esta clase.
 * Claro que el request pasar� otros datos para la identificaci�n positiva del usuario 
 * mediante un custom-validator, como su email, pero no es relevante para la regeneraci�n
 * del password.<br><br>
 * Fecha de actualizacion: 2009-05-23<br>
 * @author Martin Cordova y Asociados C.A <br>
 */
public class ResetPassword extends GenericTableManager 
{
	@Override
	public int service(Recordset inputParams) throws Throwable 
	{
		//genera password random
		String guid[] = StringUtil.split(UUID.randomUUID().toString(),"-");
		String password = guid[0];
		inputParams.setValue("passwd", password);
		inputParams.setValue("passwd_clear", password);
		
		//aplicar HASH al password
		AbstractValidator val = (AbstractValidator)getObject("dinamica.security.PasswordEncryptor");
		val.isValid(getRequest(), inputParams, null);
		
		//dejar que la clase gen�rica se encargue de actualizar la BD y notificar por email
		//el nuevo password
		super.service(inputParams);

		return 0;
	}
}
